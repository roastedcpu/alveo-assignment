## Log Monitor - Alveo assignment
J. Diogo Oliveira, Feb2021


### Requirements
docker, docker-compose, maybe a unix system because I have not tested the bindmounts on windows


### Running the project
`docker-compose up -d`


### Short description
3 microservices:
 - Log Generator generates logs and outputs them to a file [output rate can be configured under docker-compose.yml]
 - Log Monitor periodically analyses the log file and exposes a reactive API
 - Dashboard is a webpage to watch the log metrics

Access the dashboard: http://localhost:32000

Swagger: http://localhost:32001/swagger-ui/

 ### Some notes
 - The reactive API doesn't add anything to the performance since the operations are synchronous (the api returns existing objects and/or modifies simple properties). If the program could asynchronously, eg write to a db, then the reactive api (webflux) would be advisable. But I did it because I tought could be a plus to the exercise.
 - The log generator is opening the file for writing using the flag W(rite), not A(ppend) - that means if for some reason you restart the log generator service the logs will be overwritten.
 - Test coverage could be better, I just wrote some example tests but they don't really mirror what I'd do for a real scenario.
 - API spec code generation could be used to put the api in yaml files and quickly generate api models
 - Models are not really models, they should be helpers or even be in the services/ package. I'm putting them into models/ because there were no actual models
 - API logs could be more verbose and better sorted (INFO/WARN/DEBUG/etc), and also for a production version DEBUG logs could be deactivated, especially web ones
 - I should have added custom exceptions, with messages and specific http return codes, to be thrown in specific cases, the way it is now some sensitive info can pass to the client side and wrong http codes can be sent even though spring actually handles correctly most of it (except by sending stack traces as responses)
 - CORS was set to allow *
 - Regarding the frontend, no need to overengineer it. Imo and despite the fact I may be overengineered a bit the log monitor service, frameworks should be used whenever needed and I would use VueJS to build a more complex dashboard. But since it is so simple and given the time I had to put all of this together, I chose to stick with plain javascript.
 - Dashboard: A favicon is missing
 - Dashboard: No resposive behaviour testes. Materialize should handle most cases but still...
 - Dashboard: JS logic could be a little bit more resilient, validating data on both directions, checking if elements are already rendered and returning promises so that it becomes easy to read and less prone to async errors
 - Dashboard: I chose to deal with the callbacks by defining static class methods and passing the object instance. That's not a very common approach. Time is running out !
 - Dashboard: When refreshing the refreshInterval property, if you're editing the input,  it will get overwritten and that is annoying. I would fix that.
 - I'm using bindmounts on the docker side for convenience only, maybe they'll cause problems on windows, I don't have one to test
 - Therre are no real E2E tests


### How I'd scale the system:
Imagining the log file is not the bottleneck and that the API needed to respond to millions of simultaneous requests, I'd put the monitor service pushing data to a kafka topic (push/subscribe) and the API would be on the consumer side. There could be as many consumers as the requests demanded, using a scalable cluster with a HAProxy in front of the api services.
