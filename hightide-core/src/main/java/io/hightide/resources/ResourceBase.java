package io.hightide.resources;


import io.hightide.validation.ValidationError;

import java.lang.reflect.Field;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.nonNull;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public abstract class ResourceBase implements IResource {

    private String href;

    private Set<ResourceLink> links;

    private List<ValidationError> errors;

    protected ResourceBase() {}

    public ResourceBase(Map<String, Deque<String>> params) {
        for (String fieldName : params.keySet()) {
            try {
                Field field = this.getClass().getDeclaredField(fieldName);
                if (nonNull(field)) {
                    field.setAccessible(true);
                    field.set(this, params.get(fieldName));
                }
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }


    public boolean validate() {
        return false;
    }

    @Override
    public String href() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public Set<ResourceLink> getLinks() {
        return links;
    }

    @Override
    public void setLinks(Set<ResourceLink> links) {
        this.links = links;
    }

}
