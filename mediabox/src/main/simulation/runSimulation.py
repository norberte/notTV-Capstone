import subprocess

for i in range(1):
    print('Starting Subprocess '+str(i)+' of mediabox-1.0.jar')
    subprocess.call(['java','-jar','../../../target/mediabox-1.0.jar')
