# USAGE: ./start [Connections] [CSU eID]

MAX_CONN=$1    # editable
USERNAME=$2    # editable
NODE_DELAY='sleep 10;'     # editable. How long you have to setup-overlay before nodes attempt to connect to Registry.
LIB_DIR="$PWD/build/libs/"
SERVER=""
REGISTRY_SCRIPT='java -jar Homework-1/build/libs/Homework-1.jar cs455.overlay.Registry server 64001 $MAX_CONN'
NODE_SCRIPT=''

if [ -d "$LIB_DIR" ]; then
    counter=0
    command='gnome-terminal'

    for i in `cat machines.txt`
    do
        if [ $counter -eq $MAX_CONN ]; then
            break
        fi

        echo "Logging into $i."

		if [ $counter == 0 ]; then
		    SERVER="$i"
		    option='--window --title="'$i'" --command="ssh -t '$USERNAME'@'$SERVER'.cs.colostate.edu '$REGISTRY_SCRIPT' '$SHELL'"'
		else
		    NODE_SCRIPT="$NODE_DELAY java -jar Homework-1/build/libs/Homework-1.jar cs455.overlay.Registry node $SERVER.cs.colostate.edu 64001"
		    option='--tab --title="'$i'" --command="ssh -t '$USERNAME'@'$i'.cs.colostate.edu '$NODE_SCRIPT' '$SHELL'"'
		fi

		command+=" $option"
		((counter++))
	done

	eval $command

else
    echo -e "Homework-1.jar not found in ${LIB_DIR}."
fi
