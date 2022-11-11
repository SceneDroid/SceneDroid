package android.goal.explorer.model.stg.output;

import android.goal.explorer.model.stg.edge.EdgeTag;
import android.goal.explorer.model.stg.edge.TransitionEdge;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TransitionEdge")
public class OutTransitionEdge {

    private OutAbstractNode srcNode;
    private OutAbstractNode tgtNode;
    private EdgeTag edgeTag;

    public OutTransitionEdge(TransitionEdge edge) {
        if (edge.getSrcNode() == null || edge.getTgtNode() == null) {
            return;
        }
        this.srcNode = ConvertToOutput.convertNode(edge.getSrcNode());
        this.tgtNode = ConvertToOutput.convertNode(edge.getTgtNode());
        this.edgeTag = edge.getEdgeTag();
    }

    public String toString() {
        return srcNode.getName() + " ==> " + tgtNode.getName() + " Tag: " + edgeTag;
    }

    public OutAbstractNode getSrcNode() {
        return srcNode;
    }

    public OutAbstractNode getTgtNode() {
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
        if (!super.equals(other))
            return false;
        if (getClass() != other.getClass())
            return false;

        OutTransitionEdge o = (OutTransitionEdge) other;
        if (getEdgeTag() == null) {
            return o.getEdgeTag() == null;
        } else return getEdgeTag().equals(((OutTransitionEdge) other).getEdgeTag());
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
