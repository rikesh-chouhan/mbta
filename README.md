##### This project is used to list MBTA subway routes and stops information.
##### This is a java based gradle project.

#### Build info

- Ensure gradle and java(8+) are installed.
- To build this project, clone the repo.
- Go to the cloned repo directory.
- `gradle fatJar` -- Builds the jar with all the dependencies.
- *path to fatjar* -> **libs/build/mbta-all.jar**
- `java -jar` *path to fatjar* `routes`
- `java -jar` *path to fatjar* `routes stats`
- `java -jar` *path to fatjar* `routes common`

The Subway routes are obtained by using the MBTA provided filters on the API.
viz: https://api-v3.mbta.com/routes?filter\[type\]=0,1 this allows for restricting the amount
of data being returned and working only on those specific results
without having to apply filtering logic after receiving the data.

  