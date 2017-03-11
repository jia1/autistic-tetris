import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

import org.junit.Assert;
import org.junit.Test;

public class PlayerTest {

    @Test
    public void checkCreateField() {
        // Create and print
        int[][] field = createField(new String[] {
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "     X    ", 
                "     X    ", 
                " X   XX   ", 
                " XX  XXX  ", 
                " XX  X X X", 
                "XXXX XXXXX"
        });
        int[][] expectedField = new int[][] {
            {1, 1, 1, 1, 0, 1, 1, 1, 1, 1},
            {0, 1, 1, 0, 0, 1, 0, 1, 0, 1},
            {0, 1, 1, 0, 0, 1, 1, 1, 0, 0},
            {0, 1, 0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };
        //displayField(field);
        assertFieldEquals(expectedField, field);
    }
    
    @Test
    public void checkMakeMove() {
        int[][] field = createField(new String[] {
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "     X    ", 
                "     X    ", 
                " X   XX   ", 
                " XX  XXX  ", 
                " XX  X X X", 
                "XXXX XXXXX"
        });
       List<Entry<int[], int[][]>> testCases = new ArrayList<>();
       
       // Test case 1
       testCases.add(new SimpleEntry<int[], int[][]>(
                   new int[] {0, 0, 0}, // piece, orientation, column
                   createField(new String[] { // resultant board
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "YY   X    ", 
                           "YY   X    ", 
                           " X   XX   ", 
                           " XX  XXX  ", 
                           " XX  X X X", 
                           "XXXX XXXXX"
                   })
               ));
       
       // Test case 2
       testCases.add(new SimpleEntry<int[], int[][]>(
                   new int[] {4, 2, 3}, // piece, orientation, column
                   createField(new String[] { // resultant board
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "     X    ", 
                           "     X    ", 
                           " X   XX   ", 
                           " XX YXXX  ", 
                           " XXYYX X X", 
                           "XXXXYXXXXX"
                   })
               ));
       
       // Test case 3
       testCases.add(new SimpleEntry<int[], int[][]>(
                   new int[] {1, 1, 6}, // piece, orientation, column
                   createField(new String[] { // resultant board
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "          ", 
                           "     X    ", 
                           "     XYYYY", 
                           " X   XX   ", 
                           " XX  XXX  ", 
                           " XX  X X X", 
                           "XXXX XXXXX"
                   })
               ));
       for (Entry<int[], int[][]> testCase : testCases) {
           int[] move = testCase.getKey();
           int[][] expectedField = testCase.getValue();
           int[][] newField = PlayerSkeleton.applyMove(PlayerSkeleton.copyField(field), move[0], new int[]{ move[1], move[2]} );
           assertFieldEquals(expectedField, newField);
       }
    }
    
    @Test
    public void checkRows() {
        int[][] field;
        field = createField(new String[] {
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "     X    ", 
                "     X    ", 
                " X   XX   ", 
                " XX  XXX  ", 
                " XX  X X X", 
                "XXXX XXXXX"
        });
        Assert.assertEquals(0, PlayerSkeleton.countCompletedRows(field));
        assertFieldEquals(field, PlayerSkeleton.clearCompletedRows(PlayerSkeleton.copyField(field)));
        
        field = createField(new String[] {
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "     X    ", 
                "     X    ", 
                " X   XX   ", 
                " XX  XXX  ", 
                " XX  X X X", 
                "XXXXXXXXXX"
        });
        Assert.assertEquals(1, PlayerSkeleton.countCompletedRows(field));
        assertFieldEquals(createField(new String[] {
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "     X    ", 
                "     X    ", 
                " X   XX   ", 
                " XX  XXX  ", 
                " XX  X X X"
        }), PlayerSkeleton.clearCompletedRows(PlayerSkeleton.copyField(field)));
        
        field = createField(new String[] {
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "     X    ", 
                "     X    ", 
                " X   XX   ", 
                "XXXXXXXXXX", 
                " XX  X X X", 
                "XXXX XXXXX"
        });
        Assert.assertEquals(1, PlayerSkeleton.countCompletedRows(field));
        assertFieldEquals(createField(new String[] {
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "     X    ", 
                "     X    ", 
                " X   XX   ", 
                " XX  X X X", 
                "XXXX XXXXX"
        }), PlayerSkeleton.clearCompletedRows(PlayerSkeleton.copyField(field)));
        
        field = createField(new String[] {
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "XXXXXXXXXX", 
                "     X    ", 
                "     X    ", 
                " X   XX   ", 
                "XXXXXXXXXX", 
                "XXXXXXXXXX"
        });
        Assert.assertEquals(3, PlayerSkeleton.countCompletedRows(field));
        assertFieldEquals(createField(new String[] {
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "          ", 
                "     X    ", 
                "     X    ", 
                " X   XX   "
        }), PlayerSkeleton.clearCompletedRows(PlayerSkeleton.copyField(field)));
    }
    
    private static void assertFieldEquals(int[][] expected, int[][] actual) {
        Assert.assertEquals(expected.length, actual.length);
        for (int r = 0; r < expected.length; r++) {
            Assert.assertEquals(expected[r].length, actual[r].length);
            for (int c = 0; c < expected[r].length; c++) {
                Assert.assertEquals(expected[r][c], actual[r][c]);
            }
        }
    }
    
    private static void displayField(int[][] field) {
        for (int row = field.length - 1; row >= 0; row--) {
            for (int col = 0; col < field[row].length; col++) {
                System.out.print(field[row][col] == 0 ? " " : "X");
            }
            System.out.println();
        }
    }
    
    private int[][] createField(String[] map) {
        int[][] grid = new int[map.length][map[0].length()];
        for (int y = map.length - 1; y >= 0; y--) {
            for (int x = 0; x < map[0].length(); x++) {
                grid[map.length - y - 1][x] = map[y].charAt(x) == ' ' ? 0 : 1; 
            }
        }
        return grid;
    }
    
}
