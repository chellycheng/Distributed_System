#!/bin/bash 

#TODO: SPECIFY THE HOSTNAMES OF 4 CS MACHINES (lab2-8, lab2-10, etc...)
#MACHINES=(hcheng35@teach-vw2.cs.mcgill.ca, hcheng35@teach-vw1.cs.mcgill.ca, hcheng35@teach-vw3.cs.mcgill.ca, hcheng35@teach-vw4.cs.mcgill.ca)
MACHINES=(hcheng35@mimi.cs.mcgill.ca, hcheng35@mimi.cs.mcgill.ca, hcheng35@mimi.cs.mcgill.ca, hcheng35@mimi.cs.mcgill.ca)
tmux new-session \; \
	split-window -h \; \
	split-window -v \; \
	split-window -v \; \
	select-layout main-vertical \; \
	select-pane -t 1 \; \
	send-keys "ssh -t ${MACHINES[0]} \"cd $(pwd) > /dev/null; echo -n 'Connected to '; FlightServer; ./run_flightserver.sh Flights\"" C-m \; \
	select-pane -t 2 \; \
	send-keys "ssh -t ${MACHINES[1]} \"cd $(pwd) > /dev/null; echo -n 'Connected to '; RoomServer; ./run_roomserver.sh Cars\"" C-m \; \
	select-pane -t 3 \; \
	send-keys "ssh -t ${MACHINES[2]} \"cd $(pwd) > /dev/null; echo -n 'Connected to '; CarServer; ./run_carserver.sh\"" C-m \; \
	select-pane -t 0 \; \
	send-keys "ssh -t ${MACHINES[3]} \"cd $(pwd) > /dev/null; echo -n 'Connected to '; MiddlewareServer; sleep .5s; ./run_server.sh ${MACHINES[0]} ${MACHINES[1]} ${MACHINES[2]} 1018\"" C-m \;
