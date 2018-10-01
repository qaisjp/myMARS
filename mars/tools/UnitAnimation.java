package mars.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mars.Globals;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


class UnitAnimation extends JPanel
        implements ActionListener {
    /**
     *
     */
    private static final long serialVersionUID = -2681757800180958534L;

    private static final int PWIDTH = 1000;     // size of this panel
    private static final int PHEIGHT = 574;
    private final GraphicsConfiguration gc;

    private int counter;            //verify then remove.
    private boolean justStarted;    //flag to start movement


    private int indexX;    //counter of screen position
    private int indexY;
    private boolean xIsMoving, yIsMoving;        //flag for mouse movement.

    // private Vertex[][] inputGraph;
    private Vector<Vector<Vertex>> outputGraph;
    private final ArrayList<Vertex> vertexList;
    private ArrayList<Vertex> vertexTraversed;
    //Screen Label variables

    private final HashMap<String, String> registerEquivalenceTable;

    private String instructionCode;

    private final int register = 1;
    private final int control = 2;
    private final int aluControl = 3;
    private int alu = 4;
    private final int datapatTypeUsed;

    private Boolean cursorInReg;

    private Graphics2D g2d;

    private BufferedImage datapath;

    class Vertex {
        private int numIndex;
        private int init;
        private int end;
        private int current;
        private String name;
        static final int movingUpside = 1;
        static final int movingDownside = 2;
        static final int movingLeft = 3;
        static final int movingRight = 4;
        final int direction;
        int oppositeAxis;
        private boolean isMovingXaxis;
        private Color color;
        private boolean first_interaction;
        private boolean active;
        private final boolean isText;
        private final ArrayList<Integer> targetVertex;

        Vertex(int index, int init, int end, String name, int oppositeAxis, boolean isMovingXaxis,
               String listOfColors, String listTargetVertex, boolean isText) {
            this.numIndex = index;
            this.init = init;
            this.current = this.init;
            this.end = end;
            this.name = name;
            this.oppositeAxis = oppositeAxis;
            this.isMovingXaxis = isMovingXaxis;
            this.first_interaction = true;
            this.active = false;
            this.isText = isText;
            this.color = new Color(0, 153, 0);
            if (isMovingXaxis) {
                if (init < end)
                    direction = movingLeft;
                else
                    direction = movingRight;

            } else {
                if (init < end)
                    direction = movingUpside;
                else
                    direction = movingDownside;
            }
            String[] list = listTargetVertex.split("#");
            targetVertex = new ArrayList<>();
            for (String aList : list) {
                targetVertex.add(Integer.parseInt(aList));
                //	System.out.println("Adding " + i + " " +  Integer.parseInt(list[i])+ " in target");
            }
            String[] listColor = listOfColors.split("#");
            this.color = new Color(Integer.parseInt(listColor[0]), Integer.parseInt(listColor[1]), Integer.parseInt(listColor[2]));
        }

        int getDirection() {
            return direction;
        }

        public boolean isText() {
            return this.isText;
        }


        ArrayList<Integer> getTargetVertex() {
            return targetVertex;
        }

        int getNumIndex() {
            return numIndex;
        }

        public void setNumIndex(int numIndex) {
            this.numIndex = numIndex;
        }

        int getInit() {
            return init;
        }

        public void setInit(int init) {
            this.init = init;
        }

        int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        int getCurrent() {
            return current;
        }

        void setCurrent(int current) {
            this.current = current;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        int getOppositeAxis() {
            return oppositeAxis;
        }

        public void setOppositeAxis(int oppositeAxis) {
            this.oppositeAxis = oppositeAxis;
        }

        public boolean isMovingXaxis() {
            return isMovingXaxis;
        }

        public void setMovingXaxis(boolean isMovingXaxis) {
            this.isMovingXaxis = isMovingXaxis;
        }

        Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        boolean notFirst_interaction() {
            return !first_interaction;
        }

        void setFirst_interaction() {
            this.first_interaction = false;
        }

        boolean isActive() {
            return active;
        }

        void setActive(boolean active) {
            this.active = active;
        }
    }

    public UnitAnimation(String instructionBinary, int datapathType) {
        datapatTypeUsed = datapathType;
        Boolean cursorInIM = false;
        Boolean cursorInALU = false;
        Boolean cursorInDataMem = false;
        DecimalFormat df = new DecimalFormat("0.0");
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        // for reporting accl. memory usage
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

        int accelMemory = gd.getAvailableAcceleratedMemory();
        setBackground(Color.white);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        // load and initialise the images
        initImages();

        vertexList = new ArrayList<>();
        counter = 0;
        justStarted = true;
        instructionCode = instructionBinary;

        //declaration of labels definition.
        registerEquivalenceTable = new HashMap<>();

        int countRegLabel = 400;
        int countALULabel = 380;
        int countPCLabel = 380;
        loadHashMapValues();


    } // end of ImagesTests()

    //set the binnary opcode value of the basic instructions of MIPS instruction set
    private void loadHashMapValues() {
        if (datapatTypeUsed == register) {
            importXmlStringData("/registerDatapath.xml", registerEquivalenceTable);
            importXmlDatapathMap("/registerDatapath.xml");
        } else if (datapatTypeUsed == control) {
            importXmlStringData("/controlDatapath.xml", registerEquivalenceTable);
            importXmlDatapathMap("/controlDatapath.xml");
        } else if (datapatTypeUsed == aluControl) {
            importXmlStringData("/ALUcontrolDatapath.xml", registerEquivalenceTable);
            importXmlDatapathMapAluControl();
        }
    }

    //import the list of opcodes of mips set of instructions
    private void importXmlStringData(String xmlName, HashMap table) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        DocumentBuilder docBuilder;
        try {
            //System.out.println();
            docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.parse(getClass().getResource(xmlName).toString());
            Element root = doc.getDocumentElement();
            Element equivalenceItem;
            NodeList bitsList, mnemonic;
            NodeList equivalenceList = root.getElementsByTagName("register_equivalence");
            for (int i = 0; i < equivalenceList.getLength(); i++) {
                equivalenceItem = (Element) equivalenceList.item(i);
                bitsList = equivalenceItem.getElementsByTagName("bits");
                mnemonic = equivalenceItem.getElementsByTagName("mnemonic");
                for (int j = 0; j < bitsList.getLength(); j++) {
                    table.put(bitsList.item(j).getTextContent(), mnemonic.item(j).getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //import the parameters of the animation on datapath
    private void importXmlDatapathMap(String xmlName) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        DocumentBuilder docBuilder;
        try {
            docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.parse(getClass().getResource(xmlName).toString());
            Element root = doc.getDocumentElement();
            Element datapath_mapItem;
            NodeList index_vertex, name, init, end, color, other_axis, isMovingXaxis, targetVertex, sourceVertex, isText;
            NodeList datapath_mapList = root.getElementsByTagName("datapath_map");
            for (int i = 0; i < datapath_mapList.getLength(); i++) { //extract the vertex of the xml input and encapsulate into the vertex object
                datapath_mapItem = (Element) datapath_mapList.item(i);
                index_vertex = datapath_mapItem.getElementsByTagName("num_vertex");
                name = datapath_mapItem.getElementsByTagName("name");
                init = datapath_mapItem.getElementsByTagName("init");
                end = datapath_mapItem.getElementsByTagName("end");
                //definition of colors line

                if (instructionCode.substring(0, 6).equals("000000")) {//R-type instructions
                    color = datapath_mapItem.getElementsByTagName("color_Rtype");
                    //System.out.println("rtype");
                } else if (instructionCode.substring(0, 6).matches("00001[0-1]")) { //J-type instructions
                    color = datapath_mapItem.getElementsByTagName("color_Jtype");
                    //System.out.println("jtype");
                } else if (instructionCode.substring(0, 6).matches("100[0-1][0-1][0-1]")) { //LOAD type instructions
                    color = datapath_mapItem.getElementsByTagName("color_LOADtype");
                    //System.out.println("load type");
                } else if (instructionCode.substring(0, 6).matches("101[0-1][0-1][0-1]")) { //LOAD type instructions
                    color = datapath_mapItem.getElementsByTagName("color_STOREtype");
                    //System.out.println("store type");
                } else if (instructionCode.substring(0, 6).matches("0001[0-1][0-1]")) { //BRANCH type instructions
                    color = datapath_mapItem.getElementsByTagName("color_BRANCHtype");
                    //System.out.println("branch type");
                } else { //BRANCH type instructions
                    color = datapath_mapItem.getElementsByTagName("color_Itype");
                    //System.out.println("immediate type");
                }


                other_axis = datapath_mapItem.getElementsByTagName("other_axis");
                isMovingXaxis = datapath_mapItem.getElementsByTagName("isMovingXaxis");
                targetVertex = datapath_mapItem.getElementsByTagName("target_vertex");
                isText = datapath_mapItem.getElementsByTagName("is_text");

                for (int j = 0; j < index_vertex.getLength(); j++) {
                    Vertex vert = new Vertex(Integer.parseInt(index_vertex.item(j).getTextContent()), Integer.parseInt(init.item(j).getTextContent()),
                            Integer.parseInt(end.item(j).getTextContent()), name.item(j).getTextContent(), Integer.parseInt(other_axis.item(j).getTextContent()),
                            Boolean.parseBoolean(isMovingXaxis.item(j).getTextContent()), color.item(j).getTextContent(), targetVertex.item(j).getTextContent(), Boolean.parseBoolean(isText.item(j).getTextContent()));
                    vertexList.add(vert);
                }
            }
            //loading matrix of control of vertex.
            outputGraph = new Vector<>();
            vertexTraversed = new ArrayList<>();
            int size = vertexList.size();
            Vertex vertex;
            ArrayList<Integer> targetList;
            for (Vertex aVertexList : vertexList) {
                vertex = aVertexList;
                targetList = vertex.getTargetVertex();
                Vector<Vertex> vertexOfTargets = new Vector<>();
                for (Integer aTargetList : targetList) {
                    vertexOfTargets.add(vertexList.get(aTargetList));
                }
                outputGraph.add(vertexOfTargets);
            }
            for (Vector<Vertex> vert : outputGraph) {
            }

            vertexList.get(0).setActive(true);
            vertexTraversed.add(vertexList.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void importXmlDatapathMapAluControl() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        DocumentBuilder docBuilder;
        try {
            docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.parse(getClass().getResource("/ALUcontrolDatapath.xml").toString());
            Element root = doc.getDocumentElement();
            Element datapath_mapItem;
            NodeList index_vertex, name, init, end, color, other_axis, isMovingXaxis, targetVertex, sourceVertex, isText;
            NodeList datapath_mapList = root.getElementsByTagName("datapath_map");
            for (int i = 0; i < datapath_mapList.getLength(); i++) { //extract the vertex of the xml input and encapsulate into the vertex object
                datapath_mapItem = (Element) datapath_mapList.item(i);
                index_vertex = datapath_mapItem.getElementsByTagName("num_vertex");
                name = datapath_mapItem.getElementsByTagName("name");
                init = datapath_mapItem.getElementsByTagName("init");
                end = datapath_mapItem.getElementsByTagName("end");
                //definition of colors line

                if (instructionCode.substring(0, 6).equals("000000")) {//R-type instructions
                    if (instructionCode.substring(28, 32).matches("0000")) { //BRANCH type instructions
                        color = datapath_mapItem.getElementsByTagName("ALU_out010");
                        System.out.println("ALU_out010 type " + instructionCode.substring(28, 32));
                    } else if (instructionCode.substring(28, 32).matches("0010")) { //BRANCH type instructions
                        color = datapath_mapItem.getElementsByTagName("ALU_out110");
                        System.out.println("ALU_out110 type " + instructionCode.substring(28, 32));
                    } else if (instructionCode.substring(28, 32).matches("0100")) { //BRANCH type instructions
                        color = datapath_mapItem.getElementsByTagName("ALU_out000");
                        System.out.println("ALU_out000 type " + instructionCode.substring(28, 32));
                    } else if (instructionCode.substring(28, 32).matches("0101")) { //BRANCH type instructions
                        color = datapath_mapItem.getElementsByTagName("ALU_out001");
                        System.out.println("ALU_out001 type " + instructionCode.substring(28, 32));
                    } else { //BRANCH type instructions
                        color = datapath_mapItem.getElementsByTagName("ALU_out111");
                        System.out.println("ALU_out111 type " + instructionCode.substring(28, 32));
                    }
                } else if (instructionCode.substring(0, 6).matches("00001[0-1]")) { //J-type instructions
                    color = datapath_mapItem.getElementsByTagName("color_Jtype");
                    System.out.println("jtype");
                } else if (instructionCode.substring(0, 6).matches("100[0-1][0-1][0-1]")) { //LOAD type instructions
                    color = datapath_mapItem.getElementsByTagName("color_LOADtype");
                    System.out.println("load type");
                } else if (instructionCode.substring(0, 6).matches("101[0-1][0-1][0-1]")) { //LOAD type instructions
                    color = datapath_mapItem.getElementsByTagName("color_STOREtype");
                    System.out.println("store type");
                } else if (instructionCode.substring(0, 6).matches("0001[0-1][0-1]")) { //BRANCH type instructions
                    color = datapath_mapItem.getElementsByTagName("color_BRANCHtype");
                    System.out.println("branch type");
                } else {
                    color = datapath_mapItem.getElementsByTagName("color_Itype");
                    System.out.println("immediate type");
                }


                other_axis = datapath_mapItem.getElementsByTagName("other_axis");
                isMovingXaxis = datapath_mapItem.getElementsByTagName("isMovingXaxis");
                targetVertex = datapath_mapItem.getElementsByTagName("target_vertex");
                isText = datapath_mapItem.getElementsByTagName("is_text");

                for (int j = 0; j < index_vertex.getLength(); j++) {
                    Vertex vert = new Vertex(Integer.parseInt(index_vertex.item(j).getTextContent()), Integer.parseInt(init.item(j).getTextContent()),
                            Integer.parseInt(end.item(j).getTextContent()), name.item(j).getTextContent(), Integer.parseInt(other_axis.item(j).getTextContent()),
                            Boolean.parseBoolean(isMovingXaxis.item(j).getTextContent()), color.item(j).getTextContent(), targetVertex.item(j).getTextContent(), Boolean.parseBoolean(isText.item(j).getTextContent()));
                    vertexList.add(vert);
                }
            }
            //loading matrix of control of vertex.
            outputGraph = new Vector<>();
            vertexTraversed = new ArrayList<>();
            int size = vertexList.size();
            Vertex vertex;
            ArrayList<Integer> targetList;
            for (Vertex aVertexList : vertexList) {
                vertex = aVertexList;
                targetList = vertex.getTargetVertex();
                Vector<Vertex> vertexOfTargets = new Vector<>();
                for (Integer aTargetList : targetList) {
                    vertexOfTargets.add(vertexList.get(aTargetList));
                }
                outputGraph.add(vertexOfTargets);
            }
            for (Vector<Vertex> vert : outputGraph) {
            }

            vertexList.get(0).setActive(true);
            vertexTraversed.add(vertexList.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //set the initial state of the variables that controls the animation, and start the timer that triggers the animation.
    public void startAnimation(String codeInstruction) {
        instructionCode = codeInstruction;
        //config variables
        // velocity of frames in ms
        int PERIOD = 8;
        new Timer(PERIOD, this).start();    // start timer
        this.repaint();
    }

    //initialize the image of datapath.
    private void initImages() {
        try {
            BufferedImage im;
            if (datapatTypeUsed == register) {
                im = ImageIO.read(
                        getClass().getResource(Globals.imagesPath + "register.png"));
            } else if (datapatTypeUsed == control) {
                im = ImageIO.read(
                        getClass().getResource(Globals.imagesPath + "control.png"));
            } else if (datapatTypeUsed == aluControl) {
                im = ImageIO.read(
                        getClass().getResource(Globals.imagesPath + "ALUcontrol.png"));
            } else {
                im = ImageIO.read(
                        getClass().getResource(Globals.imagesPath + "alu.png"));
            }

            int transparency = im.getColorModel().getTransparency();
            datapath = gc.createCompatibleImage(
                    im.getWidth(), im.getHeight(),
                    transparency);
            g2d = datapath.createGraphics();
            g2d.drawImage(im, 0, 0, null);
            g2d.dispose();
        } catch (IOException e) {
            System.out.println("Load Image error for " +
                    getClass().getResource(Globals.imagesPath + "register.png") + ":\n" + e);
        }
    }

    public void updateDisplay() {
        this.repaint();
    }

    public void actionPerformed(ActionEvent e)
    // triggered by the timer: update, repaint
    {
        if (justStarted)
            justStarted = false;
        if (xIsMoving)
            indexX++;
        if (yIsMoving)
            indexY--;
        repaint();
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2d = (Graphics2D) g;
        // use antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        // smoother (and slower) image transformations  (e.g. for resizing)
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d = (Graphics2D) g;
        drawImage(g2d, datapath);
        executeAnimation(g);
        counter = (counter + 1) % 100;
        g2d.dispose();

    }

    private void drawImage(Graphics2D g2d, BufferedImage im) {
        if (im == null) {
            g2d.setColor(null);
            g2d.fillOval(0, 0, 20, 20);
            g2d.setColor(Color.black);
            g2d.drawString("   ", 0, 0);
        } else
            g2d.drawImage(im, 0, 0, this);
    }

    //draw lines.
    //method to draw the lines that run from left to right.
    private void printTrackLtoR(Vertex v) {
        int size;
        int[] track;
        size = v.getEnd() - v.getInit();
        track = new int[size];
        for (int i = 0; i < size; i++)
            track[i] = v.getInit() + i;
        if (v.isActive()) {
            v.setFirst_interaction();
            for (int i = 0; i < size; i++) {
                if (track[i] <= v.getCurrent()) {
                    g2d.setColor(v.getColor());
                    g2d.fillRect(track[i], v.getOppositeAxis(), 3, 3);
                }
            }
            if (v.getCurrent() == track[size - 1])
                v.setActive(false);
            v.setCurrent(v.getCurrent() + 1);
        } else if (v.notFirst_interaction()) {
            for (int i = 0; i < size; i++) {
                g2d.setColor(v.getColor());
                g2d.fillRect(track[i], v.getOppositeAxis(), 3, 3);
            }
        }

    }

    //method to draw the lines that run from right to left.
    //public boolean printTrackRtoL(int init, int end ,int currentIndex, Graphics2D g2d, Color color, int otherAxis,
//		 boolean active, boolean firstInteraction){
    private void printTrackRtoL(Vertex v) {
        int size;
        int[] track;
        size = v.getInit() - v.getEnd();
        track = new int[size];

        for (int i = 0; i < size; i++)
            track[i] = v.getInit() - i;

        if (v.isActive()) {
            v.setFirst_interaction();
            for (int i = 0; i < size; i++) {
                if (track[i] >= v.getCurrent()) {
                    g2d.setColor(v.getColor());
                    g2d.fillRect(track[i], v.getOppositeAxis(), 3, 3);
                }
            }
            if (v.getCurrent() == track[size - 1])
                v.setActive(false);

            v.setCurrent(v.getCurrent() - 1);
        } else if (v.notFirst_interaction()) {
            for (int i = 0; i < size; i++) {
                g2d.setColor(v.getColor());
                g2d.fillRect(track[i], v.getOppositeAxis(), 3, 3);
            }
        }
    }

    //method to draw the lines that run from down to top.
// public boolean printTrackDtoU(int init, int end ,int currentIndex, Graphics2D g2d, Color color, int otherAxis, 
//		 boolean active, boolean firstInteraction){
    private void printTrackDtoU(Vertex v) {
        int size;
        int[] track;

        if (v.getInit() > v.getEnd()) {
            size = v.getInit() - v.getEnd();
            track = new int[size];
            for (int i = 0; i < size; i++)
                track[i] = v.getInit() - i;
        } else {
            size = v.getEnd() - v.getInit();
            track = new int[size];
            for (int i = 0; i < size; i++)
                track[i] = v.getInit() + i;
        }

        if (v.isActive()) {
            v.setFirst_interaction();
            for (int i = 0; i < size; i++) {
                if (track[i] >= v.getCurrent()) {
                    g2d.setColor(v.getColor());
                    g2d.fillRect(v.getOppositeAxis(), track[i], 3, 3);
                }
            }
            if (v.getCurrent() == track[size - 1])
                v.setActive(false);
            v.setCurrent(v.getCurrent() - 1);

        } else if (v.notFirst_interaction()) {
            for (int i = 0; i < size; i++) {
                g2d.setColor(v.getColor());
                g2d.fillRect(v.getOppositeAxis(), track[i], 3, 3);
            }
        }
    }

    //method to draw the lines that run from top to down.
// public boolean printTrackUtoD(int init, int end ,int currentIndex, Graphics2D g2d, Color color, int otherAxis, 
//		 boolean active,  boolean firstInteraction){
    private void printTrackUtoD(Vertex v) {

        int size;
        int[] track;
        size = v.getEnd() - v.getInit();
        track = new int[size];

        for (int i = 0; i < size; i++)
            track[i] = v.getInit() + i;

        if (v.isActive()) {
            v.setFirst_interaction();
            for (int i = 0; i < size; i++) {
                if (track[i] <= v.getCurrent()) {
                    g2d.setColor(v.getColor());
                    g2d.fillRect(v.getOppositeAxis(), track[i], 3, 3);
                }

            }
            if (v.getCurrent() == track[size - 1])
                v.setActive(false);
            v.setCurrent(v.getCurrent() + 1);
        } else if (v.notFirst_interaction()) {
            for (int i = 0; i < size; i++) {
                g2d.setColor(v.getColor());
                g2d.fillRect(v.getOppositeAxis(), track[i], 3, 3);
            }
        }
    }


    //convert binnary value to integer.
    public String parseBinToInt(String code) {
        int value = 0;

        for (int i = code.length() - 1; i >= 0; i--) {
            if ("1".equals(code.substring(i, i + 1))) {
                value = value + (int) Math.pow(2, code.length() - i - 1);
            }
        }

        return Integer.toString(value);
    }

    //set and execute the information about the current position of each line of information in the animation,
    //verifies the previous status of the animation and increment the position of each line that interconnect the unit function.
    private void executeAnimation(Graphics g) {
        g2d = (Graphics2D) g;
        Vertex vert;
        for (int i = 0; i < vertexTraversed.size(); i++) {
            vert = vertexTraversed.get(i);
            if (vert.isMovingXaxis) {
                if (vert.getDirection() == Vertex.movingLeft) {
                    printTrackLtoR(vert);
                    if (!vert.isActive()) {
                        int j = vert.getTargetVertex().size();
                        Vertex tempVertex;
                        for (int k = 0; k < j; k++) {
                            tempVertex = outputGraph.get(vert.getNumIndex()).get(k);
                            boolean hasThisVertex = false;
                            for (Vertex aVertexTraversed : vertexTraversed) {
                                if (tempVertex.getNumIndex() == aVertexTraversed.getNumIndex())
                                    hasThisVertex = true;
                            }
                            if (!hasThisVertex) {
                                outputGraph.get(vert.getNumIndex()).get(k).setActive(true);
                                vertexTraversed.add(outputGraph.get(vert.getNumIndex()).get(k));
                            }
                        }
                    }
                } else {
                    printTrackRtoL(vert);
                    if (!vert.isActive()) {
                        int j = vert.getTargetVertex().size();
                        Vertex tempVertex;
                        for (int k = 0; k < j; k++) {
                            tempVertex = outputGraph.get(vert.getNumIndex()).get(k);
                            boolean hasThisVertex = false;
                            for (Vertex aVertexTraversed : vertexTraversed) {
                                if (tempVertex.getNumIndex() == aVertexTraversed.getNumIndex())
                                    hasThisVertex = true;
                            }
                            if (!hasThisVertex) {
                                outputGraph.get(vert.getNumIndex()).get(k).setActive(true);
                                vertexTraversed.add(outputGraph.get(vert.getNumIndex()).get(k));
                            }
                        }
                    }
                }
            } //end of condition of X axis
            else {
                if (vert.getDirection() == Vertex.movingDownside) {
                    if (vert.isText)
                        ;
                    else
                        printTrackDtoU(vert);

                    if (!vert.isActive()) {
                        int j = vert.getTargetVertex().size();
                        Vertex tempVertex;
                        for (int k = 0; k < j; k++) {
                            tempVertex = outputGraph.get(vert.getNumIndex()).get(k);
                            boolean hasThisVertex = false;
                            for (Vertex aVertexTraversed : vertexTraversed) {
                                if (tempVertex.getNumIndex() == aVertexTraversed.getNumIndex())
                                    hasThisVertex = true;
                            }
                            if (!hasThisVertex) {
                                outputGraph.get(vert.getNumIndex()).get(k).setActive(true);
                                vertexTraversed.add(outputGraph.get(vert.getNumIndex()).get(k));
                            }
                        }
                    }

                } else {
                    printTrackUtoD(vert);
                    if (!vert.isActive()) {
                        int j = vert.getTargetVertex().size();
                        Vertex tempVertex;
                        for (int k = 0; k < j; k++) {
                            tempVertex = outputGraph.get(vert.getNumIndex()).get(k);
                            boolean hasThisVertex = false;
                            for (Vertex aVertexTraversed : vertexTraversed) {
                                if (tempVertex.getNumIndex() == aVertexTraversed.getNumIndex())
                                    hasThisVertex = true;
                            }
                            if (!hasThisVertex) {
                                outputGraph.get(vert.getNumIndex()).get(k).setActive(true);
                                vertexTraversed.add(outputGraph.get(vert.getNumIndex()).get(k));
                            }
                        }
                    }
                }
            }
        }
    }

}
