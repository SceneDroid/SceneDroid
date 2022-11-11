import os
import sys
import shutil



def analyzeApk(apkPath, resPath, sdk):
    id=0
    logDir = resPath+"/logs"
    outputDir = resPath+"/output"
    if(not os.path.exists(logDir)): 
        os.makedirs(logDir) 
    if(not os.path.exists(outputDir)): 
        os.makedirs(outputDir) 
        
    if(os.path.exists(apkPath)): 
        apks = os.listdir(apkPath)
        extraArgs = "" #"-noLibCode "# 
        for apk in apks:
            if apk[-4:] ==".apk":
                id+=1
                print ("\n\n\nThis is the "+str(id)+"th app " +apk)
                resFile = logDir+"/"+apk[:-4]+".txt"
                if(not os.path.exists(resFile)): 
                    print("java -jar "+jarFile+"  -path "+ apkPath +" -name "+apk+" -androidJar "+ sdk +"/platforms  "+ extraArgs +" -time 30 -maxPathNumber 100 -client MainClient  -outputDir "+outputDir+" >> "+logDir+"/"+apk[:-4]+".txt")
                    os.system("java -jar "+jarFile+"  -path "+ apkPath +" -name "+apk+" -androidJar "+ sdk +"/platforms "+ extraArgs +" -time 30 -maxPathNumber 100 -client MainClient -outputDir "+outputDir+" >> "+logDir+"/"+apk[:-4]+".txt")


if __name__ == '__main__' :
    apkPath = sys.argv[1]
    resPath = sys.argv[2]
    jarFile = "ICCBot.jar"
    
    os.system("mvn -f pom.xml package -q")
    if os.path.exists("target/ICCBot.jar"):
        print("Successfully build! generate jar-with-dependencies in folder target/")
        shutil.copy("target/ICCBot.jar", jarFile)
        print("copy jar to the root directory.")
    else:
        print("Fail to build! Please run \"mvn -f pom.xml package\" to see the detail info.")
    
    sdk = "lib/"    
    analyzeApk(apkPath, resPath, sdk)
    
