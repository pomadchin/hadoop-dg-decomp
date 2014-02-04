## Hadoop Decomp v1.0.0

It is a small framework, to make some sequences of operations on huge matrix. 
[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/pomadchin/hadoop-dg-decomp/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

## Build

* The project uses [sbt](http://www.scala-sbt.org/)
* To generate IntelijIDEA project just use `sbt idea-gen`
* Generate "fat jar" only once (to keep there all dependencies) `sbt assembly`. You should not re-build this jar every time you change sources: the project uses [NoJarTool](https://github.com/ktoso/hadoop-scalding-nojartool)
* Fix project settings in `Global.scala`

We use [Travis CI](http://travis-ci.org/) to verify the build:
[![Build Status](https://secure.travis-ci.org/pomadchin/hadoop-dg-decomp.png)](https://travis-ci.org/pomadchin/hadoop-dg-decomp/) 


## Wiki

Upcoming...

## Authors
* Dmitry Kostyaev <http://twitter.com/virtuozz0>
* Grigory Pomadchin <http://twitter.com/daunnc>

## License

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
