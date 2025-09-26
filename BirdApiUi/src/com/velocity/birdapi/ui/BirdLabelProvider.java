package com.velocity.birdapi.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.velocity.itest.avian.dto.BirdDto;


/**
 * A label provider for the Bird table viewer.
 * It provides the text for each cell in the table.
 */
public class BirdLabelProvider extends ColumnLabelProvider {

    @Override
    public String getText(Object element) {
        if (element instanceof BirdDto) {
            BirdDto bird = (BirdDto) element;
            // This is a simple implementation; a more robust one would
            // handle column-specific text. This is handled by a listener.
            return bird.getName();
        }
        return super.getText(element);
    }
    
    // getText for individual columns
    public String getColumnText(Object element, int columnIndex) {
        if (!(element instanceof BirdDto)) {
            return "";
        }
        BirdDto bird = (BirdDto) element;
        switch (columnIndex) {
            case 0: // ID
                return String.valueOf(bird.getId());
            case 1: // Name
                return bird.getName();
            case 2: // Color
                return bird.getColor();
            case 3: // Weight
                return String.valueOf(bird.getWeight());
            case 4: // Height
                return String.valueOf(bird.getHeight());
            default:
                return "";
        }
    }
    
    @Override
    public Image getImage(Object element) {
        return null;
    }
}