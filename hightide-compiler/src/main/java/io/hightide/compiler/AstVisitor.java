package io.hightide.compiler;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import static com.sun.tools.javac.tree.JCTree.*;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class AstVisitor extends TreePathScanner<Void, Void> {

    protected final Trees trees;

    protected final Context context;

    protected final TreeMaker maker;

    protected final Names names;

    protected final Symtab syms;

    protected final Types types;

    private CompilationUnitTree currCompUnit;

    private JCClassDecl classDecl;

    AstVisitor(JavacTask task) {
        trees = Trees.instance(task);
        context = ((BasicJavacTask) task).getContext();
        maker = TreeMaker.instance(context);
        names = Names.instance(context);
        syms = Symtab.instance(context);
        types = Types.instance(context);
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree tree, Void p) {
        currCompUnit = tree;
        return super.visitCompilationUnit(tree, p);
    }

    @Override
    public Void visitClass(ClassTree node, Void aVoid) {
        //DebugVisitor.debug((JCClassDecl)node);
        this.classDecl = (JCClassDecl) node;

        /** Convert class default accessor to public */
        if (!hasAccessFlags(classDecl.mods)) {
            classDecl.mods = maker.Modifiers(classDecl.mods.flags | Flags.PUBLIC);
        }

        /** Override hasCode() method */

        /** Override equals() method */

        /** Override toString() method */

        return super.visitClass(node, aVoid);
    }

    @Override
    public Void visitMethod(MethodTree node, Void aVoid) {
        JCMethodDecl method = (JCMethodDecl) node;

        if (!hasAccessFlags(method.mods)) {

            /** Add public modifier */
            method.mods = maker.Modifiers(method.mods.flags | Flags.PUBLIC);
        }
        return super.visitMethod(node, aVoid);
    }

    @Override
    public Void visitVariable(VariableTree node, Void aVoid) {
        JCVariableDecl var = (JCVariableDecl) node;

        /** Skip if not class variable */
        if (!classDecl.defs.contains(var)) {
            return super.visitVariable(node, aVoid);
        }

        /** Convert class variable from default to private and create getter/setter methods */
        if (!isParameter(var.mods) && !hasAccessFlags(var.mods)) {

            /** Add private modifier */
            var.mods = maker.Modifiers(var.mods.flags | Flags.PRIVATE);

            /** Create getter for variable */
            JCMethodDecl getterDecl = maker.MethodDef(maker.Modifiers(Flags.PUBLIC),
                    names.fromString("get" + capitalizeFirstLetter(var.name.toString())),
                    var.vartype, List.nil(), List.nil(), List.nil(),
                    maker.Block(0, List.of((JCStatement) maker.Return(maker.Select(maker.This(Type.noType), var.name)))),
                    null);
//            getterDecl.sym = new Symbol.MethodSymbol(
//                    Flags.PUBLIC,
//                    names.fromString("get" + capitalizeFirstLetter(var.name.toString())),
//                    new Type.MethodType(List.nil(), var.vartype.type, List.nil(), syms.methodClass),
//                    classDecl.sym);
            classDecl.defs = classDecl.defs.append(getterDecl);

            /** Create setter for variable if not declared final */
            if (!isFinal(var.mods)) {
                JCMethodDecl setterDecl = maker.MethodDef(maker.Modifiers(Flags.PUBLIC),
                        names.fromString("set" + capitalizeFirstLetter(var.name.toString())),
                        maker.Type(new Type.JCVoidType()), List.nil(), List.nil(), List.nil(),
                        maker.Block(0, List.of((JCStatement)
                                maker.Exec(
                                        maker.Assign(
                                                maker.Select(maker.This(Type.noType), var.name),
                                                maker.Ident(var.name)
                                        )
                                )
                        )),
                        null);
//                Symbol.MethodSymbol setterSym = new Symbol.MethodSymbol(
//                                    Flags.PUBLIC,
//                                    names.fromString("set" + capitalizeFirstLetter(var.name.toString())),
//                                    new Type.MethodType(
//                                            List.of(new Type.ClassType(Type.noType, List.nil(), syms.stringType.tsym)),
//                                            syms.voidType, List.nil(), syms.methodClass),
//                                    classDecl.sym);
//                setterSym.params = List.of(new Symbol.VarSymbol(Flags.PARAMETER | Flags.EFFECTIVELY_FINAL, var.name, syms.stringType, setterSym));
//                setterDecl.sym = setterSym;
                JCVariableDecl setterParam = maker.VarDef(maker.Modifiers(Flags.PARAMETER), var.name, var.vartype, null);
//                setterParam.sym.adr = 0;
                setterDecl.params = setterDecl.params.append(setterParam);
                classDecl.defs = classDecl.defs.append(setterDecl);
            }
        }
        return super.visitVariable(var, aVoid);
    }

    private String capitalizeFirstLetter(String original) {
        if (original.length() == 0)
            return original;
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    private boolean isParameter(JCModifiers mods) {
        return 0 != (mods.flags & Flags.PARAMETER);
    }

    private boolean isFinal(JCModifiers mods) {
        return 0 != (mods.flags & Flags.FINAL);
    }

    private boolean hasAccessFlags(JCModifiers mods) {
        return 0 != (mods.flags & Flags.AccessFlags);
    }

}
