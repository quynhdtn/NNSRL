FLAG=$1
DATA_FILE=$2
MODEL_FILE=$3
OUTPUT_FILE=${4-"$PWD/output.txt"}

PYTHON_CMD="/System/Library/Frameworks/Python.framework/Versions/3.4/bin/python3.4"
DIR="/Users/quynhdo/Documents/WORKING/PhD/workspace/WE/NNSRL/Python"
CMD="export PYTHONPATH=\${PYTHONPATH}:$DIR"
$CMD
CMD="$PYTHON_CMD $DIR/liir/python/ml/LogisticRegression.py $FLAG $MODEL_FILE $DATA_FILE $OUTPUT_FILE"
echo $CMD
$CMD