package android.goal.explorer.model.stg.edge;

import android.goal.explorer.model.stg.node.AbstractNode;

import org.pmw.tinylog.Logger;

public class TransitionEdge {

    private AbstractNode srcNode;
    private AbstractNode tgtNode;
    private EdgeTag edgeTag;

    public TransitionEdge(AbstractNode srcNode, AbstractNode tgtNode) {
        this.srcNode = srcNode;
        this.tgtNode = tgtNode;
    }

    public TransitionEdge(AbstractNode srcNode, AbstractNode tgtNode, EdgeTag tag) {
        this.srcNode = srcNode;
        this.tgtNode = tgtNode;
        this.edgeTag = tag;
    }

    public String toString() {
        return srcNode.getName() + " ==> " + tgtNode.getName() + " Tag: " + edgeTag;
    }

    /**
     * Gets the source node of the edge
     * @return the source node of the edge
     */
    public AbstractNode getSrcNode() {
        return srcNode;
    }

    /**
     * Gets the target node of the edge
     * @return the target node of the edge
     */
    public AbstractNode getTgtNode() {
        return tgtNode;
    }

    /**
     * Gets the tag assigned to this edge
     * @return The tag assigned to this edge
     */
    public EdgeTag getEdgeTag() {
        return edgeTag;
    }

    /**
     * Sets the tag assigned to this edge
     * @param edgeTag The tag assigned to this edge
     */
    public void setEdgeTag(EdgeTag edgeTag) {
        this.edgeTag = edgeTag;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        /*if (!super.equals(other)){
            Logger.debug("Super issue");
            return false;
        }*/
        if (getClass() != other.getClass())
            return false;

        TransitionEdge o = (TransitionEdge) other;
        if (srcNode == null){
            if (o.getSrcNode() != null)
                return false;
        } else if (!srcNode.equals(o.getSrcNode())) 
            return false;
        if (tgtNode == null){
            if (o.getTgtNode() != null)
                return false;
        }
        else if (!tgtNode.equals(o.getTgtNode()))
            return false;  
        if (getEdgeTag() == null) {
            return o.getEdgeTag() == null;
        } else return getEdgeTag().equals(((TransitionEdge) other).getEdgeTag());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((srcNode == null) ? 0 : srcNode.hashCode());
        result = prime * result + ((tgtNode == null) ? 0 : tgtNode.hashCode());
        result = prime * result + ((edgeTag == null) ? 0 : edgeTag.hashCode());
        return result;
    }
}
