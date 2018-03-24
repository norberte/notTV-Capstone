import subprocess
import psutil
import threading
import os

"""
This code works to record cpu usage. BUT, I can't seem to get it to overwrite the old Data
when i run it again. So, you'll have to delete the file between runs or rename the old data.
"""

def printit():
    threading.Timer(1.0, printit).start()
    currentCpuUsage = psutil.cpu_percent(interval=1)
    print("CPU Usage: "+str(currentCpuUsage))
    #Append latest cpu usage report to file.
    if(os.path.exists('latestCpuUsage.csv') == True):
        with open("latestCpuUsage.csv",'a') as file:
            file.write(str(currentCpuUsage)+",")
    else:
        with open("latestCpuUsage.csv",'w') as file:
            file.write(str(currentCpuUsage)+",")

    file.close();

printit();

#subprocess.call(['python', 'recordCpuUsage.py'])

print('Starting Subprocess of mediabox-1.0.jar and recording CPU Usage every second.')
subprocess.call(['java','-jar','../../target/mediabox-1.0.jar'])
