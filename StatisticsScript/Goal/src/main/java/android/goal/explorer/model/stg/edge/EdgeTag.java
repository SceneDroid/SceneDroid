package android.goal.explorer.model.stg.edge;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("EdgeTag")
public class EdgeTag {

    private String typeOfUiElement;
    private String handlerMethod;
    private Integer resId;
    private Integer parentId;
    private String text;
    private String contentDesc;

    public EdgeTag(String typeOfUiElement, String handlerMethod, Integer resId) {
        this.typeOfUiElement = typeOfUiElement;
        this.handlerMethod = handlerMethod;
        this.resId = resId;
    }

    public EdgeTag(String typeOfUiElement, String handlerMethod, Integer resId, String contentDesc, String text){
        this.typeOfUiElement = typeOfUiElement;
        this.handlerMethod = handlerMethod;
        this.resId = resId;
        this.text = text;
        this.contentDesc = contentDesc;
    }


    public String getHandlerMethod(){
        return this.handlerMethod;
    }

    public String getTypeOfUiElement(){
        return this.typeOfUiElement;
    }

    public String getText(){
        return this.text;
    }

    public String getContentDesc(){
        return this.contentDesc;
    }

    public Integer getResId(){
        return this.resId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        if(parentId != null && parentId != -1)
            this.parentId = parentId;
    }

    @Override
    public String toString() {
        return String.valueOf(resId);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (typeOfUiElement == null ? 0 : typeOfUiElement.hashCode());
        result = prime * result + (handlerMethod == null ? 0 : handlerMethod.hashCode());
        result = prime * result + (resId == null ? 0 : resId.hashCode());
        result = prime * result + (contentDesc == null ? 0: contentDesc.hashCode());
        result = prime * result + (text == null ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        /*if (!super.equals(obj))
            return false;*/
        if (getClass() != obj.getClass())
            return false;

        EdgeTag other = (EdgeTag) obj;
        //What about handler method ?
        if (handlerMethod == null) {
            return other.handlerMethod == null;
        }
        else if(!other.handlerMethod.equals(handlerMethod))
            return false;
        if (resId == null) {
            return other.resId == null;
        } 
        else if (!resId.equals(other.resId)){
            return false;
        }
        if (contentDesc == null) {
            if (other.contentDesc != null)
                return false;
        }
        else if (!contentDesc.equals(other.contentDesc))
            return false;
        if (text == null)
            return other.text == null;
        return text.equals(other.text);
        
    }
}
