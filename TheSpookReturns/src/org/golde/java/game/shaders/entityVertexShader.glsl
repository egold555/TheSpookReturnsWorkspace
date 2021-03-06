#version 150

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[8];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPos[8];

uniform float useFakeLightning;

uniform float density;
uniform float gradient;

uniform vec4 plane;

void main(void){

    vec4 worldPosition = transformationMatrix * vec4(position,1.0);
    
    gl_ClipDistance[0] = dot(worldPosition, plane);

	vec4 positionRelativeToCam =  viewMatrix * worldPosition;
	
	gl_Position = projectionMatrix * positionRelativeToCam;
	pass_textureCoords = textureCoords;
	
	vec3 actualNormal = normal;
	if(useFakeLightning >0.5){
	    actualNormal = vec3(0.0, 1.0, 0.0);
	}
	
	surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
	for(int i = 0; i < 8; i++){
		toLightVector[i] = lightPos[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
}