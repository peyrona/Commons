/*
 * Copyright (C) 2015 Francisco José Morero Peyrona. All Rights Reserved.
 *
 * GNU Classpath is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the free
 * Software Foundation; either version 3, or (at your option) any later version.
 *
 * This app is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this software; see the file COPYING.  If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.peyrona.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Esta clase tiene un conjunto de métodos para facilitar la búsqueda de nodos en
 * ficheros XML. No es la manera má eficiente de procesar XMLs y por ello no debe
 * utilizarse con ficheros enormes (de muchos megas), pero es muy cómodo y
 * bastante rápido.
 *
 * @author peyrona
 */
public class XMLHelper
{
    public static Element getRoot( File fXML ) throws ParserConfigurationException, SAXException, IOException
    {
        return getRoot( new FileInputStream( fXML ) );
    }

    public static Element getRoot( URL urlXML ) throws ParserConfigurationException, SAXException, IOException
    {
        return getRoot( urlXML.openStream() );
    }

    public static Element getRoot( InputStream isXML ) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
        DocumentBuilder        builder  = factory.newDocumentBuilder();
        Document               document = builder.parse( isXML );
                               document.getDocumentElement().normalize();

        return document.getDocumentElement();
    }

    /**
     * Returns the first node in the passed List which name is equals to the
     * passed one or null if none of them matches.
     *
     * @param nodeName
     * @param nodes
     * @return
     */
    public static Node getNode( String nodeName, NodeList nodes )
    {
        for( int n = 0; n < nodes.getLength(); n++ )
        {
            Node node = nodes.item( n );

            if( node.getNodeName().equalsIgnoreCase( nodeName ) )
            {
                return node;
            }
        }

        return null;
    }

    /**
     * Returns the nodeValue of the first node which name matches the passed
     * name inside the passed List.
     *
     * and nodeValue
     * @param nodeName
     * @param nodes
     * @return
     */
    public static String getNodeValue( String nodeName, NodeList nodes )
    {
        return getNodeValue( getNode( nodeName, nodes ) );
    }

    /**
     * Returns the first child node of passed one which node type is
     * Node.TEXT_NODE.
     *
     * @param node
     * @return
     */
    public static String getNodeValue( Node node )
    {
        if( node != null )
        {
            NodeList childNodes = node.getChildNodes();

            for( int n = 0; n < childNodes.getLength(); n++ )
            {
                Node data = childNodes.item( n );

                if( data.getNodeType() == Node.TEXT_NODE )
                {
                    return data.getNodeValue();
                }
            }
        }

        return "";
    }

    /**
     * Returns all the nodes in the passed List which name is equals to the
     * passed one.
     *
     * @param nodeName
     * @param nodes
     * @return
     */
    public static List<Node> getNodeAll( String nodeName, NodeList nodes )
    {
        List<Node> lstNodes = new ArrayList<>();

        for( int n = 0; n < nodes.getLength(); n++ )
        {
            Node node = nodes.item( n );

            if( node.getNodeName().equalsIgnoreCase( nodeName ) )
            {
                lstNodes.add( node );
            }
        }

        return lstNodes;
    }

    /**
     * Devuelve el valor (TEXT) para todos las entradas cuyo nombre sea el valor
     * del parámetro "tagName".
     *
     * @param tagName
     * @param nodes
     * @return
     */
    public static List<String> getNodeAllValues( String tagName, NodeList nodes )
    {
        List<Node>   lstNodes = getNodeAll( tagName, nodes );
        List<String> values   = new ArrayList<>();

        for( Node node : lstNodes )
        {
            NodeList childNodes = node.getChildNodes();

            for( int n = 0; n < childNodes.getLength(); n++ )
            {
                Node data = childNodes.item( n );

                if( data.getNodeType() == Node.TEXT_NODE )
                {
                    values.add( data.getNodeValue() );
                }
            }
        }

        return values;
    }

    /**
     * Dado un nodo, devuelve el valor del atributo indicado o "" si el
     * atributo no está presente en el nodo.
     *
     * @param node
     * @param attrName
     * @return
     */
    public static String getNodeAttrValue( Node node, String attrName )
    {
        NamedNodeMap attrs = node.getAttributes();

        for( int n = 0; n < attrs.getLength(); n++ )
        {
            Node attr = attrs.item( n );

            if( attr.getNodeName().equalsIgnoreCase( attrName ) )
            {
                return attr.getNodeValue();
            }
        }

        return "";
    }

    public static ParamMap getNodeAttributes( Node node )
    {
        ParamMap     params  = new ParamMap();
        NamedNodeMap lstAttr = node.getAttributes();

        for( int n = 0; n < lstAttr.getLength(); n++ )
        {
            Node attr = lstAttr.item( n );
            params.put( attr.getNodeName(), attr.getNodeValue() );
        }

        return params;
    }
}