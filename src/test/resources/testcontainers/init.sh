## Quando viene aggiornato questo file, aggiornare anche il commitId presente nel file initsh-for-testcontainer-sh

echo " - Create pn-timeline-service TABLES"

aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name Timelines \
    --attribute-definitions \
        AttributeName=iun,AttributeType=S \
        AttributeName=timelineElementId,AttributeType=S \
    --key-schema \
        AttributeName=iun,KeyType=HASH \
        AttributeName=timelineElementId,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5

aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name PnDeliveryPushShedLock \
    --attribute-definitions \
        AttributeName=_id,AttributeType=S \
    --key-schema \
        AttributeName=_id,KeyType=HASH \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5

aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name TimelinesCounters \
    --attribute-definitions \
        AttributeName=timelineElementId,AttributeType=S \
    --key-schema \
        AttributeName=timelineElementId,KeyType=HASH \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5

echo "Initialization terminated"
