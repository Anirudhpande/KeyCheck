# KeyCheck

This is a mini project which I am making for Vectra.

Earlier all we were able to was just verify the API key if its working or not, so right now I've made some features in which now u can chat with the api key,
and get the responses

This is the core of VECTRA which is nothing but a BYOK platform.

I have implemented the AES/GCM cryptographic encryption and decryption to encrypt and decrypt the api keys.

I have tried to optimize the code and tried to implement proper implementation of abstraction that i could think of at the moment. 
Earlier KeyCheck was doing everything, now It's merely just an entry point in our program.

I have implemented a basic implementation of giving the context of the earlier prompts of the user and responses of the AI by just adding everything into an array keeping the record of every prompt made by the user and response by the AI.

I have to do something about this Chat History because at the moment I am just sending the whole conversation between the user and AI as the context which is just increasing the token counts in every prompt, which is really really bad. 

I need to figure out how do you do that.

Next what i am planning to do is that somehow reduce the token count of every prompt
