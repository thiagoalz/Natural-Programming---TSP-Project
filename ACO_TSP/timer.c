



#include <stdio.h>
#include <time.h>

#include "timer.h"


clock_t start_time;

double elapsed;


void start_timers()
/*    
      FUNCTION:       virtual and real time of day are computed and stored to 
                      allow at later time the computation of the elapsed time 
		      (virtual or real) 
      INPUT:          none
      OUTPUT:         none
      (SIDE)EFFECTS:  virtual and real time are computed   
*/
{
    start_time = clock();
}



double elapsed_time( type )
	TIMER_TYPE type;
/*    
      FUNCTION:       return the time used in seconds (virtual or real, depending on type) 
      INPUT:          TIMER_TYPE (virtual or real time)
      OUTPUT:         seconds since last call to start_timers (virtual or real)
      (SIDE)EFFECTS:  none
*/
{
    elapsed = clock()- start_time;
    return elapsed / CLOCKS_PER_SEC;
}



