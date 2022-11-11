import os
import sys
import shutil

def analyzeApk(apkPath, resPath, sdk, tag):

    logDir = resPath+"/logs"+tag
    outputDir = resPath+"/output"+tag
    if(not os.path.exists(logDir)): 
        os.makedirs(logDir) 
    if(not os.path.exists(outputDir)): 
        os.makedirs(outputDir) 
        
    if(os.path.exists(apkPath)): 
        apks = os.listdir(apkPath)
        for apk in apks:
            if apk[-4:] ==".apk":
                print("java -jar ICCBot.jar  -path "+ apkPath +" -name "+apk+" -androidJar "+ sdk +"/platforms -time 30 -maxPathNumber 100 -client ToolEvaluateClient -outputDir "+outputDir+" >> "+logDir+"/"+apk[:-4]+".txt")
                os.system("java -jar ICCBot.jar  -path "+ apkPath +" -name "+apk+" -androidJar "+ sdk +"/platforms -time 60 -maxPathNumber 100 -client ToolEvaluateClient -outputDir "+outputDir+" >> "+logDir+"/"+apk[:-4]+".txt")


if __name__ == '__main__' :
    apkPath = sys.argv[1]
    resPath = sys.argv[2]
    tag =""
    
    jarFile = "ICCBot.jar"
    
    os.system("mvn -f pom.xml package -q")
    if os.path.exists("target/ICCBot.jar"):
        print("Successfully build! generate jar-with-dependencies in folder target/")
        shutil.copy("target/ICCBot.jar", jarFile)
        print("copy jar to the root directory.")
    else:
        print("Fail to build! Please run \"mvn -f pom.xml package\" to see the detail info.")
    
    sdk = "lib/"    
    analyzeApk(apkPath, resPath, sdk,tag)
    
