import os.path


class iccbotres:
    def __init__(self, root_dir):
        '''

        :param root_dir: icc result dir path
        '''
        self.root_dir = root_dir
        self.callgraph = os.path.join(self.root_dir, "CallGraphInfo")
        self.ctg = os.path.join(self.root_dir, "CTGResult")
        self.fragment = os.path.join(self.root_dir, "FragmentInfo")
        self.iccsep = os.path.join(self.root_dir, "ICCSpecification")
        self.manifest = os.path.join(self.root_dir, "ManifestInfo")
        self.soot = os.path.join(self.root_dir, "SootIRInfo")
        print("[root] : ", self.root_dir)
        print("[CallGraphInfo] : ", self.callgraph)
        print("[CTGResult] : ", self.ctg)
        print("[FragmentInfo] : ", self.fragment)
        print("[ICCSpecification] : ", self.iccsep)
        print("[ManifestInfo] : ", self.manifest)
        print("[SootIRInfo] : ", self.soot)


