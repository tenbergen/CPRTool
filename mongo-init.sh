#!/bin/bash

docker exec -it cpr-mongo bash -c 'mongosh --port 27037 -- "$MONGO_INITDB_DATABASE" <<EOF
    var rootUser = "$MONGO_INITDB_ROOT_USERNAME";
    var rootPassword = "$MONGO_INITDB_ROOT_PASSWORD";
    var admin = db.getSiblingDB("admin");
    admin.auth(rootUser, rootPassword);
    db.createUser({user: "$MONGO_USERNAME", pwd: "$MONGO_PASSWORD", roles: [{role: "readWrite", db: "$MONGO_INITDB_DATABASE"}]});
EOF'

docker exec -it cpr-mongo2 bash -c 'mongosh --port 27038 -- "$MONGO_INITDB_DATABASE" <<EOF
    var rootUser = "$MONGO_INITDB_ROOT_USERNAME";
    var rootPassword = "$MONGO_INITDB_ROOT_PASSWORD";
    var admin = db.getSiblingDB("admin");
    admin.auth(rootUser, rootPassword);
    db.createUser({user: "$MONGO_USERNAME", pwd: "$MONGO_PASSWORD", roles: [{role: "readWrite", db: "$MONGO_INITDB_DATABASE"}]});
EOF'

docker exec -it cpr-mongo3 bash -c 'mongosh --port 27039 -- "$MONGO_INITDB_DATABASE" <<EOF
    var rootUser = "$MONGO_INITDB_ROOT_USERNAME";
    var rootPassword = "$MONGO_INITDB_ROOT_PASSWORD";
    var admin = db.getSiblingDB("admin");
    admin.auth(rootUser, rootPassword);
    db.createUser({user: "$MONGO_USERNAME", pwd: "$MONGO_PASSWORD", roles: [{role: "readWrite", db: "$MONGO_INITDB_DATABASE"}]});
EOF'

docker exec -it cpr-mongo4 bash -c 'mongosh --port 27040 -- "$MONGO_INITDB_DATABASE" <<EOF
    var rootUser = "$MONGO_INITDB_ROOT_USERNAME";
    var rootPassword = "$MONGO_INITDB_ROOT_PASSWORD";
    var admin = db.getSiblingDB("admin");
    admin.auth(rootUser, rootPassword);
    db.createUser({user: "$MONGO_USERNAME", pwd: "$MONGO_PASSWORD", roles: [{role: "readWrite", db: "$MONGO_INITDB_DATABASE"}]});
EOF'

docker exec -it cpr-mongo5 bash -c 'mongosh --port 27041 -- "$MONGO_INITDB_DATABASE" <<EOF
    var rootUser = "$MONGO_INITDB_ROOT_USERNAME";
    var rootPassword = "$MONGO_INITDB_ROOT_PASSWORD";
    var admin = db.getSiblingDB("admin");
    admin.auth(rootUser, rootPassword);
    db.createUser({user: "$MONGO_USERNAME", pwd: "$MONGO_PASSWORD", roles: [{role: "readWrite", db: "$MONGO_INITDB_DATABASE"}]});
EOF'