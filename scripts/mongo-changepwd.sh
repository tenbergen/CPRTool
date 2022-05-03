#!/bin/bash

# Run this script to update all mongodb user password.

docker exec -it cpr-mongo bash -c 'mongosh --port 27037 -- "$MONGO_INITDB_DATABASE" <<EOF
    var rootUser = "$MONGO_INITDB_ROOT_USERNAME";
    var rootPassword = "$MONGO_INITDB_ROOT_PASSWORD";
    var admin = db.getSiblingDB("admin");
    admin.auth(rootUser, rootPassword);
    db.changeUserPassword("$MONGO_USERNAME", "$MONGO_PASSWORD")
EOF'

docker exec -it cpr-mongo2 bash -c 'mongosh --port 27038 -- "$MONGO_INITDB_DATABASE" <<EOF
    var rootUser = "$MONGO_INITDB_ROOT_USERNAME";
    var rootPassword = "$MONGO_INITDB_ROOT_PASSWORD";
    var admin = db.getSiblingDB("admin");
    admin.auth(rootUser, rootPassword);
    db.changeUserPassword("$MONGO_USERNAME", "$MONGO_PASSWORD")
EOF'

docker exec -it cpr-mongo3 bash -c 'mongosh --port 27039 -- "$MONGO_INITDB_DATABASE" <<EOF
    var rootUser = "$MONGO_INITDB_ROOT_USERNAME";
    var rootPassword = "$MONGO_INITDB_ROOT_PASSWORD";
    var admin = db.getSiblingDB("admin");
    admin.auth(rootUser, rootPassword);
    db.changeUserPassword("$MONGO_USERNAME", "$MONGO_PASSWORD")
EOF'

docker exec -it cpr-mongo4 bash -c 'mongosh --port 27040 -- "$MONGO_INITDB_DATABASE" <<EOF
    var rootUser = "$MONGO_INITDB_ROOT_USERNAME";
    var rootPassword = "$MONGO_INITDB_ROOT_PASSWORD";
    var admin = db.getSiblingDB("admin");
    admin.auth(rootUser, rootPassword);
    db.changeUserPassword("$MONGO_USERNAME", "$MONGO_PASSWORD")
EOF'

docker exec -it cpr-mongo5 bash -c 'mongosh --port 27041 -- "$MONGO_INITDB_DATABASE" <<EOF
    var rootUser = "$MONGO_INITDB_ROOT_USERNAME";
    var rootPassword = "$MONGO_INITDB_ROOT_PASSWORD";
    var admin = db.getSiblingDB("admin");
    admin.auth(rootUser, rootPassword);
    db.changeUserPassword("$MONGO_USERNAME", "$MONGO_PASSWORD")
EOF'