/*
 *  Armadillo Workflow Platform v1.0
 *  A simple pipeline system for phylogenetic analysis
 *  
 *  Copyright (C) 2009-2011  Etienne Lord, Mickael Leclercq
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package configuration;

/**
 * Helper class to time function
 * @author Etienne Lord
 * @since 2010
 */
public class Timer {
    static long previous_time_ns=0;
    static long previous_time_ms=0;

    public static void reset() {
        previous_time_ns=System.nanoTime();
        previous_time_ms=System.currentTimeMillis();
    }

    public static long timeMS() {
        long timems=System.currentTimeMillis()-previous_time_ms;
        previous_time_ns=System.nanoTime();
        previous_time_ms=System.currentTimeMillis();
        return timems;

    }
    public static long timeNS() {
        long timens=System.nanoTime()-previous_time_ns;
        previous_time_ns=System.nanoTime();
        previous_time_ms=System.currentTimeMillis();
        return timens;
    }
}
