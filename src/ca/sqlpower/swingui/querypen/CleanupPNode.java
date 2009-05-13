/*
 * Copyright (c) 2009, SQL Power Group Inc.
 *
 * This file is part of Wabit.
 *
 * Wabit is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Wabit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */

package ca.sqlpower.swingui.querypen;

/**
 * Objects that use Piccolo2d can implement this interface to have a cleanup
 * method for handling any kinds of disconnecting or other operations when the
 * view is being removed.
 */
public interface CleanupPNode {

    /**
     * Causes this object to unlisten to its model object and anything else
     * that could impede garbage collection. Once you have cleaned up a
     * WabitNode, you can't use it again.
     */
    void cleanup();
}
