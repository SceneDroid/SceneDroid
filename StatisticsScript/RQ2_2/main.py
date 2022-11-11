import csv
import os
simpleRows = []
RQ2_res = "./Res"

def checkRes(res_path):
    global simpleRows
    pkgName = ""
    actlist = set()
    actcover = set()
    SecondAct = set()
    ICCPath = os.path.join(res_path, "iccbot")
    if os.path.exists(ICCPath):
        print("[+] Successful ", ICCPath)
    else:
        print("[-} Fault ", ICCPath)
        return
    tmp = ""
    for file in os.listdir(ICCPath):
        tmp = file
    ICCPath = os.path.join(ICCPath, tmp)
    if os.path.exists(ICCPath):
        print("[+] Successful ", ICCPath)
    else:
        print("[-} Fault ", ICCPath)
        return
    ManifestPath = os.path.join(ICCPath, "ManifestInfo", "AndroidManifest.txt")
    if os.path.exists(ManifestPath):
        print("[+] Successful ", ManifestPath)
    else:
        print("[-} Fault ", ManifestPath)
        return
    textlines = []
    with open(ManifestPath, "r", encoding="utf8") as f:
        textlines = f.readlines()
    for index in range(len(textlines)):
        if 'package: ' in textlines[index]:
            # print(textlines[index])
            pkgName = textlines[index].split("- package: ")[-1].split("\n")[0]
            # print(pkgName)
        if 'activity' in textlines[index]:
            if '- name: ' in textlines[index + 2]:
                # print(textlines[index+2])
                act = textlines[index + 2].split('- name: ')[-1].split("\n")[0]
                actlist.add(act)
    print("[actlist] : ", actlist)


    Second = os.path.join(res_path, "SecondStart.txt")
    if os.path.exists(Second):
        print("[+] Successful ", Second)
    else:
        print("[-} Fault ", Second)
        return
    textlines = []
    with open(Second, "r", encoding="utf8") as f:
        textlines = f.readlines()
    for index in range(len(textlines)):
        SecondAct.add(textlines[index].split("\n")[0])
    print(SecondAct)

    firstStart = set()
    successActPath = os.path.join(res_path, "succeACT.txt")
    if os.path.exists(successActPath):
        print("[+] Successful ", successActPath)
    else:
        print("[-} Fault ", successActPath)
        return
    textlines = []
    with open(successActPath, "r", encoding="utf8") as f:
        textlines = f.readlines()
    for index in range(len(textlines)):
        tmp = textlines[index].split("\n")[0]
        firstStart.add(tmp)
    print(firstStart)

    addAct = set()
    for sec in SecondAct:
        if sec not in firstStart:
            addAct.add(sec)

    print("[+] ADD ACT: ", len(addAct))
    # simpleH = ['Name', 'TotalActNum', 'FirstStart', 'SecondStart', 'Upgrade']
    simpleD = {'Name': pkgName}
    simpleD['TotalActNum'] = len(actlist)
    simpleD['FirstStart'] = len(firstStart)
    simpleD['SecondStart'] = len(addAct)
    simpleD['Upgrade'] = float(len(addAct)/len(firstStart))
    simpleRows.append(simpleD)


    '''
    if os.path.exists(ICCPath):
        print("[+] Successful ", ICCPath)
    else:
        print("[-} Fault ", ICCPath)
        return
    ManifestPath = os.path.join(ICCPath, "ManifestInfo", "AndroidManifest.txt")
    if os.path.exists(ManifestPath):
        print("[+] Successful ", ManifestPath)
    else:
        print("[-} Fault ", ManifestPath)
        return
    textlines = []
    with open(ManifestPath, "r", encoding="utf8") as f:
        textlines = f.readlines()
    for index in range(len(textlines)):
        if 'package: ' in textlines[index]:
            #print(textlines[index])
            pkgName = textlines[index].split("- package: ")[-1].split("\n")[0]
            #print(pkgName)
        if 'activity' in textlines[index]:
            if '- name: ' in textlines[index+2]:
                #print(textlines[index+2])
                act = textlines[index+2].split('- name: ')[-1].split("\n")[0]
                actlist.append(act)
    print("[actlist] : ", actlist)
    transPath = os.path.join(res_path, "trans.txt")
    if os.path.exists(transPath):
        print("[+] Successful ", transPath)
    else:
        print("[-} Fault ", transPath)
        return
    textlines = []
    with open(transPath, "r", encoding="utf8") as f:
        textlines = f.readlines()
    coverall = set()
    for index in range(len(textlines)):
        right = textlines[index].split('->')[0]
        left = textlines[index].split('->')[-1].split("\n")[0]
        if right in actlist:
            print("[right]", right)
            actcover.add(right)
            coverall.add(right)
        if left in actlist:
            print("[left]", left)
            coverall.add(left)
            #actcover.add(left)
            pass
    print(actcover)
    firstStart = []
    successActPath = os.path.join(res_path, "succeACT.txt")
    if os.path.exists(successActPath):
        print("[+] Successful ", successActPath)
    else:
        print("[-} Fault ", successActPath)
        return
    textlines = []
    with open(successActPath, "r", encoding="utf8") as f:
        textlines = f.readlines()
    for index in range(len(textlines)):
        tmp = textlines[index].split("\n")[0]
        firstStart.append(tmp)
    print(firstStart)
    ourSuccess = []
    for index in actcover:
        if index not in firstStart:
            ourSuccess.append(index)
    print(ourSuccess)

    simpleD = {'Name': pkgName}
    simpleD['TotalActNum'] = len(actlist)
    simpleD['TotalCover'] = len(coverall)
    print(coverall)
    simpleD['FirstStart'] = len(firstStart)
    simpleD['SecondStart'] = len(ourSuccess)

    simpleRows.append(simpleD)'''

if __name__ == '__main__':
    pkgres_list = []
    for pkgres in os.listdir(RQ2_res):
        pkgres_list.append(pkgres)
        print(pkgres)
        real_path = os.path.join(RQ2_res, pkgres)
        if os.path.exists(real_path):
            print("[+] Successful ", pkgres)
            checkRes(real_path)
        else:
            print("[-} Fault ", pkgres)

    simpleH = ['Name', 'TotalActNum', 'FirstStart', 'SecondStart', 'Upgrade']
    with open('RQ2.csv', 'w') as f:
        f_csv = csv.DictWriter(f, simpleH)
        f_csv.writeheader()
        f_csv.writerows(simpleRows)
