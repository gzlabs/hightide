package actions;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class Posts  {

    public String index() {
        return "index";
    }

    public String show(Long id) {
        return "show" + id;
    }

    public void create() {}

    public void save() {

    }

    public void delete(Long id) {

    }

    public void update(Long id) {

    }

    public String edit(Long id) {
        return "edit";
    }

}
