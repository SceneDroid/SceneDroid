import os
import threading
import time
import os, sys, traceback, subprocess

#output
outDir = "results/outputDir/"
#instrument
tag = "M_ICCTAG K_ICCTAG"
#apk 
apkId = 1

#monkey
count = 10000
throttle = 100
num = 10

#ape
apeTime = 60
manualTime = 600
val = "1"

class DownThread:
    def __init__(self):
        self._running = True

    def terminate(self):
        self._running = False

    def run(self):
        print("adb logcat -c")
        os.system("adb logcat -c")
        logDir = outDir + appName+ "/instrument/" 
        if(not os.path.exists(logDir)):
            os.makedirs(logDir)
        logFile = logDir+appName+"_log_"+val+".txt"
        print("adb logcat -s "+tag+" >> " +logFile)
        os.system("adb logcat -s "+tag+" >> " +logFile)
        while self._running:
            pass
        print ("c.terminate()")


def log2File():
    os.system("adb start-server")
    print ("start logging...")
    c = DownThread()
    t = threading.Thread(target=c.run, args=())
    t.start()
    time.sleep(3)
    return c
  
def useManual():
    c = log2File()
    print("adb shell monkey -p "+package+" 10")
    os.system("adb shell monkey -p "+package+" 10")
    time.sleep(manualTime)
    print("adb stop")
    os.system("adb kill-server")
    c.terminate()
    print ("end logging.")
    time.sleep(3)
    
def useMonkey(appName, package):
    for i in range(num):
        c=log2File()
        print ("start monkey...")
        print("adb shell monkey -p "+package+" --throttle "+str(throttle)+" "+str(count))
        os.system("adb shell monkey -p "+package+" --throttle "+str(throttle)+" "+str(count))
        print ("end monkey.") 
        print("adb stop")
        os.system("adb kill-server")
        c.terminate()
        print ("end logging.")
        time.sleep(3)
        

    
def useAPE(appName, package):
    c=log2File()    
    try:
        #-p com.mikifus.padland --running-minutes 100 --ape sata
        run_ape(appName, package)
    except:
        traceback.print_exc()
    print("adb stop")
    os.system("adb kill-server")
    c.terminate()
    print ("end logging.")
    time.sleep(10)    
    
def run_ape(appName, package):
    ADB=os.getenv('ADB', 'adb')
    APE_ROOT='/data/local/tmp/'
    APE_JAR=APE_ROOT + 'ape.jar'
    APE_MAIN='com.android.commands.monkey.Monkey'
    APP_PROCESS='/system/bin/app_process'
    SERIAL=os.getenv('SERIAL')
    if SERIAL:
        BASE_CMD=[ADB, '-s', SERIAL, 'shell', 'CLASSPATH=' + APE_JAR, APP_PROCESS, APE_ROOT, APE_MAIN, '-p', package, '--running-minutes', str(apeTime), '--ape', 'sata']
    else:
        BASE_CMD=[ADB, 'shell', 'CLASSPATH=' + APE_JAR, APP_PROCESS, APE_ROOT, APE_MAIN, '-p', package, '--running-minutes', str(apeTime), '--ape', 'sata']
    run_cmd(BASE_CMD )

    
def run_cmd(*args):
    print('Run cmd: ' + (' '.join(*args)))
    subprocess.check_call(*args)

    
def getApkInfoFromFile():
    appInfoList = []
    f = open("scripts/pkgNames.txt","r")
    lines = f.readlines()
    f.close()
    for line in lines:
        ss = line.strip().split("\t")
        info = [ss[0],ss[1]]
        appInfoList.append(info)
    return appInfoList       

# 0 CSipSimple	com.csipsimple
# 1 1Sheeld	com.integreight.onesheeld
# 2 Padland	com.mikifus.padland
# 3 Calendula	es.usc.citius.servando.calendula
# 4 SuntimesWidget	com.forrestguice.suntimeswidget
# 5 Conversations	eu.siacs.conversations
# 6 AntennaPod	de.danoeh.antennapod
# 7 AnkiDroid	com.ichi2.anki
# 8 PassAndroid	org.ligi.passandroid
# 9 AFWall+	dev.ukanth.ufirewall
# 10 OpenKeychain	org.sufficientlysecure.keychain
# 11 SteamGifts	net.mabako.steamgifts
# 12 Simple-Solitaire	de.tobiasbielefeld.solitaire
# 13 iNaturalist	org.inaturalist.android
# 14 syncthing	com.nutomic.syncthingandroid
# 15 K9Mail	com.fsck.k9
# 16 Lincal	felixwiemuth.lincal
# 17 OpenGPSTracker	nl.sogeti.android.gpstracker
# 18 EteSync	com.etesync.syncadapter
# 19 ch.hgdev.toposuite	ch.hgdev.toposuite
# 20 eu.vranckaert.worktime	eu.vranckaert.worktime
# 21 fr.ybo.transportsrennes	fr.ybo.transportsrennes
# 22 me.blog.korn123.easydiary	me.blog.korn123.easydiary
# 23 net.osmand.plus	net.osmand.plus
# 24 com.farmerbb.taskbar	com.farmerbb.taskbar
# 25 org.epstudios.epmobile	org.epstudios.epmobile
# 26 org.liberty.android.fantastischmemo	org.liberty.android.fantastischmemo
# 27 org.smssecure.smssecure	org.smssecure.smssecure
# 28 org.tasks	org.tasks
# 29 pt.joaomneto.titancompanion	pt.joaomneto.titancompanion
# 30 com.oriondev.moneywallet	com.oriondev.moneywallet
# 31 Evercam	io.evercam.androidapp

if __name__ == '__main__' :
    appInfoList = getApkInfoFromFile()
    id = 22
    for apkId in range(id, id+9):
        appName = appInfoList[apkId][0]
        package = appInfoList[apkId][1]
        print (appName, package)
        #useMonkey(appName, package)
        useAPE(appName, package)
        #useManual()