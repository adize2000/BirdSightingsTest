package com.velocity.birdapi.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.velocity.itest.avian.dto.SightingDto;


/**
 * A label provider for the Sighting table viewer.
 * It provides the text for each cell in the table.
 */
public class SightingLabelProvider extends ColumnLabelProvider {
    
    public String getColumnText(Object element, int columnIndex) {
        if (!(element instanceof SightingDto)) {
            return "";
        }
        SightingDto sighting = (SightingDto) element;
        switch (columnIndex) {
            case 0: // ID
                return String.valueOf(sighting.getId());
            case 1: // Bird Name
                return sighting.getBird() != null ? sighting.getBird().getName() : "N/A";
            case 2: // Location
                return sighting.getLocation();
            case 3: // Date-Time
                return sighting.getDateTime() != null ? sighting.getDateTime().toString() : "";
            default:
                return "";
        }
    }
    
    @Override
    public Image getImage(Object element) {
        return null;
    }
}