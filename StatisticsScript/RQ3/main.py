import csv
import os

resDir = "./Res"
projectPathList = []
simpleRows = []

def buildProject():
    global project_list
    if os.path.exists(resDir):
        print("[+] RQ3 Result Dir is exists!")
    else:
        print("[-] RQ3 Result Dir is not exists!")
        exit(0)
    for project in os.listdir(resDir):
        if 'DS_Store' not in project:
            # print(os.path.join(resDir, project))
            projectPathList.append(os.path.join(resDir, project))


def ParesProject(path):
    pkgname = ""
    prlist = []
    prpath = []
    for sp in os.listdir(path):
        pkgname = sp.split("-")[0]
        break
    print("[+] PKG : ", pkgname)
    for sp in os.listdir(path):
        # print(sp)
        prlist.append(sp)
        prpath.append(os.path.join(path, sp))
    print(prlist)
    print(prpath)
    for index in range(len(prlist) - 1):
        p2p(prpath[index], prpath[index + 1], prlist[index], prlist[index + 1])


def p2p(p1, p2, p1n, p2n):
    p1ScL = []
    p1TrL = []
    p2ScL = []
    p2TrL = []
    print("[P1] : ", p1)
    print("[P2] : ", p2)
    p1ScPath = os.path.join(p1, "screenshot")
    p2ScPath = os.path.join(p2, "screenshot")
    if os.path.exists(p1ScPath):
        print("[+] p1ScPath is exists!")
    else:
        print("[-] p1ScPath is not exists!")
        return
    if os.path.exists(p2ScPath):
        print("[+] p2ScPath is exists!")
    else:
        print("[-] p2ScPath is not exists!")
        return
    for sc in os.listdir(p1ScPath):
        p1ScL.append(sc.split(".png")[0])
    for sc in os.listdir(p2ScPath):
        p2ScL.append(sc.split(".png")[0])
    # print(p1ScL)
    # print(p2ScL)
    changesc = set()
    for sc in p2ScL:
        if sc not in p1ScL:
            changesc.add(sc)
    print("[+] Change SC: ", changesc)
    p1TrPath = os.path.join(p1, "trans.txt")
    p2TrPath = os.path.join(p2, "trans.txt")
    if os.path.exists(p1TrPath):
        print("[+] p1TrPath is exists!")
    else:
        print("[-] p1TrPath is not exists!")
        return
    if os.path.exists(p2TrPath):
        print("[+] p2TrPath is exists!")
    else:
        print("[-] p2TrPath is not exists!")
        return
    p1TrL = set()
    p2TrL = set()
    textlines = []
    with open(p1TrPath, "r", encoding="utf8") as f:
        textlines = f.readlines()
    for index in range(len(textlines)):
        tmp = textlines[index].split("\n")[0]
        p1TrL.add(tmp)
    with open(p2TrPath, "r", encoding="utf8") as f:
        textlines = f.readlines()
    for index in range(len(textlines)):
        tmp = textlines[index].split("\n")[0]
        p2TrL.add(tmp)
    #print(p1TrL)
    #print(p2TrL)
    changetr = set()
    for tr in p2TrL:
        if tr not in p1TrL:
            changetr.add(tr)
    print("[+] Change TR: ", changetr)
    name = p1n + "->" + p2n
    simpleD = {'APK1->APK2': name}
    simpleD['ChangeScreen'] = len(changesc)
    simpleD['ChangeTrans'] = len(changetr)
    simpleRows.append(simpleD)


if __name__ == '__main__':
    buildProject()
    for project in projectPathList:
        ParesProject(project)
    simpleH = ['APK1->APK2', 'ChangeScreen', 'ChangeTrans']
    with open("RQ3.csv", "w") as f:
        f_csv = csv.DictWriter(f, simpleH)
        f_csv.writeheader()
        f_csv.writerows(simpleRows)
