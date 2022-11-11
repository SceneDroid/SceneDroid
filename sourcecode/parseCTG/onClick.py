def clickparse(rootIR, obj):
    '''
    :param rootIR:
    :param jimple:
    :param obj:
    :return:
    '''
    print("[PARSE Click]")
    rid = ""
    try:
        with open(rootIR, 'r') as f:
            #print(obj.sootir)
            irlines = f.readlines()
            #print(len(irlines))
            for index in range(len(irlines)):
                if irlines[index].strip().startswith("specialinvoke") and obj.sootir in irlines[index].strip():
                    #print(irlines[index].strip())
                    res_1 = irlines[index].strip().split('specialinvoke ')[1].split('.<')[0]
                    #print(res_1)
                    for son1index in range(index, len(irlines)):
                        if irlines[son1index].strip().startswith("virtualinvoke") and res_1 in irlines[son1index].strip() and "void setOnClickListener" in irlines[son1index]:
                            #print(irlines[son1index].strip())
                            break
                    res_2 = irlines[son1index].strip().split('virtualinvoke ')[1].split('.<')[0]
                    #print(res_2)
                    for son2index in range(son1index, 0, -1):
                        #print(irlines[son2index].strip())
                        if irlines[son2index].strip().startswith(res_2) and "android.widget" in irlines[son2index].strip():
                            #print(irlines[son2index].strip())
                            break
                    res_3 = "$" + irlines[son2index].strip().split('$')[1].split(';')[0]
                    #print(res_3)
                    for son3index in range(son2index, 0, -1):
                        if irlines[son3index].strip().startswith(res_3) and "findViewById" in irlines[son3index].strip():
                            #print(irlines[son3index].strip())
                            break
                    rid = irlines[son3index].strip().split("findViewById(int)>(")[1].split(");")[0]
                    #print(rid)
    except:
        pass
    if rid != "":
        obj.rid = rid

