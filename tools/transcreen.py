'''
def parsetrans(project):
    print("================ Pare Trans ================")
    screenobjlist = project.screenobject
    print(screenobjlist)
    for screenobj in screenobjlist:
        screenobj.printAll()
    for screenobj in screenobjlist:
        sonobj = ""
        if screenobj.svector != "":
            for son in screenobjlist:
                if son.vector == screenobj.svector:
                    sonobj = son

        print("#[SCREEN] : ", screenobj.vector)
        tmptrans = ""
        if screenobj.stype == True:
            if screenobj.sact != "":
                tmptrans = screenobj.act + "->" + screenobj.sact
            elif screenobj.svector != "":
                tmptrans = screenobj.act + "->" + screenobj.svector
            project.inittrans.add(tmptrans)

        else:
            if screenobj.sact != "":
                tmptrans = screenobj.vector + "->" + screenobj.sact
            if screenobj.newfrag:
                if screenobj.nextfragment != "":
                    tmptrans = screenobj.fatfragment + "->" + screenobj.fragment
                else:
                    tmptrans = screenobj.fatfragment + "->" + screenobj.svector
            else:
                if screenobj.nextfragment != "":
                    tmptrans = screenobj.vector + "->" + screenobj.nextfragment
                else:
                    tmptrans = screenobj.vector + "->" + screenobj.svector
            project.inittrans.add(tmptrans)
'''


def parseNewActScreen(fatherobj, project):
    father = ""
    son = ""
    if fatherobj.stype:
        print("[FATHER ACTIVITY] : ", fatherobj.act)
        father = fatherobj.act
    elif fatherobj.newfrag:
        print("[FATHER FRAGMENT] : ", fatherobj.fragment)
        father = fatherobj.fragment
    else:
        print("[FATHER VECTOR] : ", fatherobj.vector)
        father = fatherobj.vector
    for nextact in fatherobj.nextact:
        tmptrans = father + "->" + nextact
        project.inittrans.add(tmptrans)

    for sonobj in fatherobj.sonScreen:
        print("[+] Get New Son Screen : ", sonobj.vector)
        if sonobj.newfrag:
            print("[SON FRAGMENT] : ", sonobj.fragment)
            son = sonobj.fragment
        else:
            print("[SON VECTOR] : ", sonobj.vector)
            son = sonobj.vector
        tmptrans = father + "->" + son
        project.inittrans.add(tmptrans)


def parsetrans(project):
    print("================ Pare Trans ================")
    screenobjlist = project.screenobject
    print(screenobjlist)
    for screenobj in screenobjlist:
        print("#[SCREEN] : ", screenobj.vector)
        parseNewActScreen(screenobj, project)
