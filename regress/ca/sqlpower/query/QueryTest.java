/*
 * Copyright (c) 2009, SQL Power Group Inc.
 *
 * This file is part of SQL Power Library.
 *
 * SQL Power Library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * SQL Power Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */

package ca.sqlpower.query;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ca.sqlpower.sql.JDBCDataSource;
import ca.sqlpower.sqlobject.SQLDatabase;
import ca.sqlpower.sqlobject.SQLDatabaseMapping;

public class QueryTest extends TestCase {

    private class StubDatabaseMapping implements SQLDatabaseMapping {

        public SQLDatabase getDatabase(JDBCDataSource ds) {
            return null;
        }
        
    }
    
    private class TestingStringItem extends StringItem {
        
        private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
        
        public TestingStringItem(String name) {
            super(name);
        }
        
        @Override
        public Item createCopy() {
            return new TestingStringItem(getName());
        }
        
        @Override
        public void addPropertyChangeListener(PropertyChangeListener l) {
            super.addPropertyChangeListener(l);
            listeners.add(l);
        }
        
        public List<PropertyChangeListener> getListeners() {
            return listeners;
        }
    }
    
    private class TestingItemContainer extends ItemContainer {
        
        private final List<ContainerChildListener> listeners = new ArrayList<ContainerChildListener>();
        
        public TestingItemContainer(String name) {
            super(name);
        }
        
        @Override
        public Container createCopy() {
            return new TestingItemContainer(getName());
        }
        
        @Override
        public void addChildListener(ContainerChildListener l) {
            super.addChildListener(l);
            listeners.add(l);
        }
        
        public List<ContainerChildListener> getListeners() {
            return listeners;
        }
    }
    
    private class TestingSQLJoin extends SQLJoin {
        
        private final List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
        
        public TestingSQLJoin(Item leftItem, Item rightItem) {
            super(leftItem, rightItem);
        }
        
        @Override
        public SQLJoin createCopy(Item leftItemCopy, Item rightItemCopy) {
            return new TestingSQLJoin(leftItemCopy, rightItemCopy);
        }
        
        @Override
        public void addJoinChangeListener(PropertyChangeListener l) {
            super.addJoinChangeListener(l);
            listeners.add(l);
        }
        
        public List<PropertyChangeListener> getListeners() {
            return listeners;
        }
        
    }
    
    /**
     * Tests the copy constructor of a query will copy columns, containers, 
     * and joins.
     */
    public void testCopyConstructorWithoutListeners() throws Exception {
        Query query = new Query(new StubDatabaseMapping());
        Item leftColumn = new TestingStringItem("leftCol");
        Container leftContainer = new TestingItemContainer("leftContainer");
        leftContainer.addItem(leftColumn);
        Item rightColumn = new TestingStringItem("rightCol");
        Container rightContainer = new TestingItemContainer("rightContainer");
        rightContainer.addItem(rightColumn);
        SQLJoin join = new TestingSQLJoin(leftColumn, rightColumn);
        query.addTable(leftContainer);
        query.addTable(rightContainer);
        query.addJoin(join);
        
        assertTrue(query.getFromTableList().contains(leftContainer));
        assertTrue(query.getFromTableList().contains(rightContainer));
        assertTrue(query.getJoins().contains(join));
        
        Query copy = new Query(query, false);
        
        assertEquals(2, copy.getFromTableList().size());
        assertEquals(1, copy.getJoins().size());
        
        final Container copiedContainers1 = copy.getFromTableList().get(0);
        final Container copiedContainers2 = copy.getFromTableList().get(1);
        final SQLJoin copiedJoin = copy.getJoins().iterator().next();
        
        assertTrue(copy.getJoinMapping().containsKey(copiedContainers1));
        assertTrue(copy.getJoinMapping().containsKey(copiedContainers2));
        assertTrue(copy.getJoinMapping().get(copiedContainers1).containsAll(copy.getJoins()));
        assertTrue(copy.getJoinMapping().get(copiedContainers2).containsAll(copy.getJoins()));
        assertNotSame(copiedContainers1, leftContainer);
        assertNotSame(copiedContainers2, leftContainer);
        assertNotSame(copiedContainers1, rightContainer);
        assertNotSame(copiedContainers2, rightContainer);
        
        assertEquals(0, ((TestingItemContainer) copiedContainers1).getListeners().size());
        assertEquals(0, ((TestingItemContainer) copiedContainers2).getListeners().size());
        assertEquals(0, ((TestingSQLJoin) copiedJoin).getListeners().size());
        for (Item item : copiedContainers1.getItems()) {
            assertEquals(0, ((TestingStringItem) item).getListeners().size());
        }
        for (Item item : copiedContainers2.getItems()) {
            assertEquals(0, ((TestingStringItem) item).getListeners().size());
        }
    }
    
    /**
     * Tests the copy constructor of a query will copy columns, containers, 
     * and joins.
     */
    public void testCopyConstructorWithListeners() throws Exception {
        Query query = new Query(new StubDatabaseMapping());
        Item leftColumn = new TestingStringItem("leftCol");
        Container leftContainer = new TestingItemContainer("leftContainer");
        leftContainer.addItem(leftColumn);
        Item rightColumn = new TestingStringItem("rightCol");
        Container rightContainer = new TestingItemContainer("rightContainer");
        rightContainer.addItem(rightColumn);
        SQLJoin join = new TestingSQLJoin(leftColumn, rightColumn);
        query.addTable(leftContainer);
        query.addTable(rightContainer);
        query.addJoin(join);
        
        assertTrue(query.getFromTableList().contains(leftContainer));
        assertTrue(query.getFromTableList().contains(rightContainer));
        assertTrue(query.getJoins().contains(join));
        
        Query copy = new Query(query, true);
        
        assertEquals(2, copy.getFromTableList().size());
        assertEquals(1, copy.getJoins().size());
        
        final Container copiedContainers1 = copy.getFromTableList().get(0);
        final Container copiedContainers2 = copy.getFromTableList().get(1);
        final SQLJoin copiedJoin = copy.getJoins().iterator().next();
        
        assertTrue(copy.getJoinMapping().containsKey(copiedContainers1));
        assertTrue(copy.getJoinMapping().containsKey(copiedContainers2));
        assertTrue(copy.getJoinMapping().get(copiedContainers1).containsAll(copy.getJoins()));
        assertTrue(copy.getJoinMapping().get(copiedContainers2).containsAll(copy.getJoins()));
        assertNotSame(copiedContainers1, leftContainer);
        assertNotSame(copiedContainers2, leftContainer);
        assertNotSame(copiedContainers1, rightContainer);
        assertNotSame(copiedContainers2, rightContainer);
        
        assertEquals(1, ((TestingItemContainer) copiedContainers1).getListeners().size());
        assertTrue(((TestingItemContainer) copiedContainers1).getListeners().contains(copy.getTableChildListener()));
        assertEquals(1, ((TestingItemContainer) copiedContainers2).getListeners().size());
        assertTrue(((TestingItemContainer) copiedContainers2).getListeners().contains(copy.getTableChildListener()));
        assertEquals(1, ((TestingSQLJoin) copiedJoin).getListeners().size());
        assertTrue(((TestingSQLJoin) copiedJoin).getListeners().contains(copy.getJoinChangeListener()));
        for (Item item : copiedContainers1.getItems()) {
            assertEquals(1, ((TestingStringItem) item).getListeners().size());
            assertTrue(((TestingStringItem) item).getListeners().contains(copy.getItemListener()));
        }
        for (Item item : copiedContainers2.getItems()) {
            assertEquals(1, ((TestingStringItem) item).getListeners().size());
            assertTrue(((TestingStringItem) item).getListeners().contains(copy.getItemListener()));
        }
    }
    
    /**
     * This is a test to confirm that a query string can be created from a Query
     * object if the database is missing. This is for cases where the database
     * may not be able to be connected to.
     */
    public void testQueryConstructionWithMissingDB() throws Exception {
        Query query = new Query(new StubDatabaseMapping());
        Container container = new ItemContainer("Test_Table");
        Item item = new StringItem("column");
        container.addItem(item);
        query.addTable(container);
        item.setSelected(true);
        
        assertEquals(1, query.getSelectedColumns().size());
        assertTrue(query.getSelectedColumns().contains(item));
        
        String queryString = query.generateQuery();
        queryString = queryString.toLowerCase();
        
        System.out.println(queryString);
        assertTrue(queryString.toLowerCase().matches("select(.|\r|\n)*test_table.column(.|\r|\n)*from(.|\r|\n)*test_table(.|\r|\n)*test_table(.|\r|\n)*"));
    }
    
}