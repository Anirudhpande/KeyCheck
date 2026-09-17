# KeyCheck

This is a mini project which I am making for Vectra.

Earlier all we were able to was just verify the API key if its working or not, so right now I've made some features in which now u can chat with the api key,
and get the responses

This is the core of VECTRA which is nothing but a BYOK platform.

I have implemented the AES/GCM cryptographic encryption and decryption to encrypt and decrypt the api keys.

so the flow of your program is that first the user gives the provider name, and API key, then we encrypt the API key, if the API key is accepted then we stored it in an object cause this is a mini project
and we do not have a database as of now, after the object is stored, for all the prompts or query made to the encrypted API is first decrypted and if its verifies then only you get the response, else the program throws an exception.

so the next thing which i am gonna do in the project is that i will try to clean up my code and optimize it.
