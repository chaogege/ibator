/*
 *  Copyright 2008 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements;

import org.apache.ibatis.ibator.api.FullyQualifiedTable;
import org.apache.ibatis.ibator.api.IntrospectedColumn;
import org.apache.ibatis.ibator.api.dom.xml.Attribute;
import org.apache.ibatis.ibator.api.dom.xml.TextElement;
import org.apache.ibatis.ibator.api.dom.xml.XmlElement;

/**
 * 
 * @author Jeff Butler
 *
 */
public class DeleteByPrimaryKeyElementGenerator extends AbstractXmlElementGenerator {

    public DeleteByPrimaryKeyElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("delete"); //$NON-NLS-1$
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();

        answer.addAttribute(new Attribute(
                "id", "del" + table.getDomainObjectName())); //$NON-NLS-1$
//        FullyQualifiedJavaType parameterClass;
//        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
//            parameterClass = introspectedTable.getPrimaryKeyType();
//        } else {
//            parameterClass = introspectedTable.getBaseRecordType();
//        }
//        answer.addAttribute(new Attribute("parameterClass", //$NON-NLS-1$
//                parameterClass.getFullyQualifiedName()));

//        ibatorContext.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("delete from "); //$NON-NLS-1$
        sb.append(table.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("  and "); //$NON-NLS-1$
            } else {
                sb.append("where "); //$NON-NLS-1$
                and = true;
            }

            sb.append(introspectedColumn.getEscapedColumnName());
            sb.append(" in "); //$NON-NLS-1$
            answer.addElement(new TextElement(sb.toString()));
            
            XmlElement dynamicElement = new XmlElement("iterate"); //$NON-NLS-1$
            dynamicElement.addAttribute(new Attribute("open", ")"));
            dynamicElement.addAttribute(new Attribute("close", "("));
            dynamicElement.addAttribute(new Attribute("conjunction", ","));
            dynamicElement.addElement(new TextElement("#[]"+ introspectedColumn.getJdbcTypeName() +"#"));
            answer.addElement(dynamicElement);
        }

        if (ibatorContext.getPlugins().sqlMapDeleteByPrimaryKeyElementGenerated(answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
