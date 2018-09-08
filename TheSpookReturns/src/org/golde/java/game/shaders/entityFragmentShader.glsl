#version 150

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[8];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColor[8];
uniform vec3 attenuation[8];
uniform vec3 lightSpotDir[8];       // for spot lighting
uniform float lightInnerConeCos[8]; // for spot lighting, -1 if not a spot.
uniform float lightOuterConeCos[8]; // for spot lighting, -2 if not a spot
uniform float shineDamper;
uniform float reflectivity;
uniform float ambientLighting;
uniform vec3 skyColor;

void main(void){

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);
    
    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
    
    for(int i = 0; i < 8; i++){
 	    vec3 unitLightVector = normalize(toLightVector[i]);
	    vec3 lightDirection = -unitLightVector;

        // Spot intensity from 0.0 to 1.0 makes spot lights work.
        // Non-spot lights will be 1.0.
        float cosLightAngle = dot(lightDirection, lightSpotDir[i]);
        float innerMinusOuterCos = lightInnerConeCos[i] - lightOuterConeCos[i];
        float spotIntensity = clamp((cosLightAngle - lightOuterConeCos[i]) / innerMinusOuterCos, 0.0, 1.0);
 	    
    	float distance = length(toLightVector[i]);
    	float attFactor = (attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance)) / spotIntensity;
	    float nDotl = dot(unitNormal, unitLightVector);
	    float brightness = max(nDotl, 0);
	    
	    vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
	
	    float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
	    specularFactor = max(specularFactor, 0.0);
	    float dampedFactor = pow(specularFactor, shineDamper);
	    totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attFactor;
	    totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attFactor;
    }
    totalDiffuse = max(totalDiffuse, ambientLighting);
    
    vec4 textureColor = texture(textureSampler, pass_textureCoords);
    if(textureColor.a < 0.5){
        discard;
    }
    
	out_Color = vec4(totalDiffuse, 1.0) *  textureColor + vec4(totalSpecular, 1.0);
	out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
}