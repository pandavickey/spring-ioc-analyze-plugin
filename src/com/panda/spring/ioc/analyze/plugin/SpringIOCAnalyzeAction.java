package com.panda.spring.ioc.analyze.plugin;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author panda
 */
public class SpringIOCAnalyzeAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        VirtualFile moduleFile = DataKeys.VIRTUAL_FILE.getData(dataContext);
        if (Objects.isNull(moduleFile)) {
            Messages.showMessageDialog(
                    "无效的目录",
                    "SpringIOC依赖分析",
                    Messages.getQuestionIcon());
            return;
        }
        Component component = event.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        JDialog dialog = showLoadingDialog(component);
        List<String> cycles = analyze(moduleFile);
        dialog.dispose();
        showResultMessageDialog(component, cycles);
    }

    JDialog showLoadingDialog(Component component) {
        JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(component), "SpringIOC依赖分析中......", false);
        dialog.setSize(400, 50);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog;
    }

    List<String> analyze(VirtualFile moduleFile) {
        DsfCycle dsfCycle = new DsfCycle();
        List<String> filePaths = getFilePaths(moduleFile);
        for (String filePath : filePaths) {
            CompilationUnit unit = null;
            try {
                unit = JavaParser.parse(new File(filePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (unit != null) {
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
                ClassOrInterfaceDeclaration classDeclaration = unit.getClassByName(fileName).orElse(null);
                if (classDeclaration != null) {
                    List<FieldDeclaration> fields = classDeclaration.getFields();
                    for (FieldDeclaration field : fields) {
                        if (field.getAnnotationByName("Autowired").isPresent()) {
                            dsfCycle.addLine(fileName, field.getElementType().asString());
                        }
                    }
                }
            }
        }
        return dsfCycle.find();
    }

    void showResultMessageDialog(Component component, List<String> cycles) {
        if (cycles.size() > 0) {
            StringBuilder result = new StringBuilder();
            result.append("发现 ");
            result.append(cycles.size());
            result.append(" 处循环依赖");
            result.append("\n\n");
            for (String c : cycles) {
                result.append(c).append("\n");
            }
            JTextArea textArea = new JTextArea(result.toString());
            textArea.setBackground(JOptionPane.getRootFrame().getBackground());
            textArea.setBorder(null);
            textArea.setEditable(false);
            textArea.setRows(10);
            JBScrollPane pane = new JBScrollPane(textArea);
            pane.setBorder(null);
            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(component),
                    pane,
                    "SpringIOC依赖分析",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            Messages.showMessageDialog("无循环依赖",
                    "SpringIOC依赖分析",
                    Messages.getInformationIcon());
        }
    }

    List<String> getFilePaths(VirtualFile file) {
        List<String> files = new ArrayList<String>();
        VirtualFile[] tempList = file.getChildren();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isDirectory()) {
                files.addAll(getFilePaths(tempList[i]));
            } else {
                files.add(tempList[i].getCanonicalPath());
            }
        }
        return files;
    }


}
