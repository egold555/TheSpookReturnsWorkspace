#version 150

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector[8];
in vec3 toCameraVector;
in float visibility;
in vec4 shadowCoords;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform sampler2D shadowMap;

uniform vec3 lightColor[8];
uniform vec3 attenuation[8];
uniform vec3 lightSpotDir[8];       // for spot lighting
uniform float lightInnerConeCos[8]; // for spot lighting, -1 if not a spot.
uniform float lightOuterConeCos[8]; // for spot lighting, -2 if not a spot
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

const int pcfCount = 2; //1 = 3x3   2 = 5x5   3 = 7x7   etc
const float totalTexels = (pcfCount * 2.0 + 1.0) *  (pcfCount * 2.0 + 1.0);

void main(void){

	float mapSize = 4096.0; //ShadowMasterRenderer.java
	float texelSize = 1.0 / mapSize;
	float total = 0.0;
	
	for(int x = -pcfCount; x<=pcfCount; x++){
		for(int y = -pcfCount; y<=pcfCount; y++){
			float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x, y) * texelSize).r;
			if(shadowCoords.z > objectNearestLight){
				total += 1.0;
			}
		}
	}
	
	total /= totalTexels;

	float lightFactor = 1.0 - (total * shadowCoords.w);
	
	vec4 blendMapColor = texture(blendMap, pass_textureCoordinates);
	
	float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
	vec2 tiledCoords = pass_textureCoordinates * 40;
	
	vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColor = texture(rTexture, tiledCoords) * blendMapColor.r;
	vec4 gTextureColor = texture(gTexture, tiledCoords) * blendMapColor.g;
	vec4 bTextureColor = texture(bTexture, tiledCoords) * blendMapColor.b;
	
	vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;
	
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitVectorToCamera = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
 	
	for (int i = 0; i < 8; ++i) {
		vec3 unitLightVector = normalize(toLightVector[i]);
		vec3 lightDirection = -unitLightVector;

        // Spot intensity from 0.0 to 1.0 makes spot lights work.
        // Non-spot lights will be 1.0.
        float cosLightAngle = dot(lightDirection, lightSpotDir[i]);
        float innerMinusOuterCos = lightInnerConeCos[i] - lightOuterConeCos[i];
        float spotIntensity = clamp((cosLightAngle - lightOuterConeCos[i]) / innerMinusOuterCos, 0.0, 1.0);
 
     	float distance = length(toLightVector[i]);
    	float attFactor = (attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance)) / spotIntensity;
		
		float nDotl = dot(unitNormal,unitLightVector);
		float brightness = max(nDotl,0);
		
		vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
		
		float specularFactor = dot(reflectedLightDirection , unitVectorToCamera);
		specularFactor = max(specularFactor,0.0);
		float dampedFactor = pow(specularFactor,shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attFactor;
	}
	
	totalDiffuse = max(totalDiffuse * lightFactor, 0.2);

	out_Color =  vec4(totalDiffuse,1.0) * totalColor + vec4(totalSpecular,1.0);
	out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
}