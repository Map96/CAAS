./mongod --config ../config/mongod.conf --dbpath ../CacheDb

sudo systemctl start redis
sudo systemctl stop redis

./connect-standalone.sh ../config/connect-standalone.properties ../config/mongo-source.properties
./kafka-topics.sh --bootstrap-server=localhost:9092 --list
./kafka-topics.sh --bootstrap-server localhost:9092 --topic cacheDb.Correlation --create
./kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic cacheDb.Correlation