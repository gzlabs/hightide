package io.hightide.enjos;

import java.util.Date;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */

class TestClass {

    public TestClass() {}

//    TestClass(Long id, String name, String description, Date aDate, Boolean aBool) {}

    static Date aDate;

    final Boolean aBool = true;

    String description;

    public String aPublicField;

    public final Long id = 1L;

    private String name;

    String getName() {
        return this.name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof TestClass)) return false;

        TestClass testClass = (TestClass) o;

        if (aBool != null ? !aBool.equals(testClass.aBool) : testClass.aBool != null) return false;
        if (aPublicField != null ? !aPublicField.equals(testClass.aPublicField) : testClass.aPublicField != null)
            return false;
        if (description != null ? !description.equals(testClass.description) : testClass.description != null)
            return false;
        if (id != null ? !id.equals(testClass.id) : testClass.id != null) return false;
        if (name != null ? !name.equals(testClass.name) : testClass.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = aBool != null ? aBool.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (aPublicField != null ? aPublicField.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}