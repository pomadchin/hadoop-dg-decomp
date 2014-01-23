## Hadoop Decomp v1.0.0

It is a small framework, to make some sequences of operations on huge matrix. 

## How to

* The project uses [sbt](http://www.scala-sbt.org/)
* To generate idea project just use `sbt idea-gen`
* Generate "fat jar" only once (to keep there all dependencies) `sbt assembly`. You should not re-build this jar every time you change sources: the project uses [NoJarTool](https://github.com/ktoso/hadoop-scalding-nojartool)
* Fix project settings in `Global.scala`

## Wiki

Upcoming...

## Authors
* Dmitry Kostyaev <http://twitter.com/virtuozz0>
* Grigory Pomadchin <http://twitter.com/daunnc>

## License

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
