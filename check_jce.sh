JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
AES=`jrunscript -e "print (javax.crypto.Cipher.getMaxAllowedKeyLength('AES') >= 256)"`
RC5=`jrunscript -e "print (javax.crypto.Cipher.getMaxAllowedKeyLength('RC5') >= 256)"`

if [ "$AES" == "true" ] && [ "$RC5" == "true" ]; then
    echo true
else
    echo false
fi;
