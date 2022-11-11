def opitemparse(rootIR, obj):
    rid = ""
    print("[PARSE OPTION ITEM]")
    try:
        with open(rootIR, 'r') as f:
            #print(obj.sootir)
            irlines = f.readlines()
            #print(len(irlines))
            for index in range(len(irlines)):
                if obj.fun in irlines[index].strip():
                    break
            for son1index in range(index, len(irlines)):
                if irlines[son1index].strip().startswith("specialinvoke") and obj.source.split(obj.used_name+".")[-1] in irlines[son1index].strip():
                    #print(irlines[son1index].strip())
                    break
            for son2index in range(son1index, 0, -1):
                if irlines[son2index].strip().startswith("label"):
                    #print(irlines[son2index].strip())
                    label = irlines[son2index].strip().split(":")[0]
                    break
            if label == "":
                rid = ""
            else:
                #print("label: ", label)
                for son3index in range(son2index, 0, -1):
                    if "goto " + label in irlines[son3index].strip() and "case" in irlines[son3index].strip():
                        #print(irlines[son3index].strip())
                        break
                rid = irlines[son3index].strip().split("case ")[-1].split(":")[0]
    except:
        pass
    if rid != "":
        obj.rid = rid