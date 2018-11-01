package se.experiment.threedimensions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class DemoViewer {

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		Container pane = frame.getContentPane();
		pane.setLayout(new BorderLayout());

		// slider to control horizontal rotation
		JSlider headingSlider = new JSlider(0, 360, 180);
		pane.add(headingSlider, BorderLayout.SOUTH);

		// slider to control vertical rotation
		JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL);
		pane.add(pitchSlider, BorderLayout.EAST);

		// panel to display render results
		JPanel renderPanel = new JPanel() {
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(Color.BLACK);
				g2.fillRect(0, 0, getWidth(), getHeight());

				List<Triangle> tris = new ArrayList<>();

				// tetrahedron
				/*
				 * tris.add(new Triangle(new Vertex(100, 100, 100), new
				 * Vertex(-100, -100, 100), new Vertex(-100, 100, -100),
				 * Color.WHITE)); tris.add(new Triangle(new Vertex(100, 100,
				 * 100), new Vertex(-100, -100, 100), new Vertex(100, -100,
				 * -100), Color.RED)); tris.add(new Triangle(new Vertex(-100,
				 * 100, -100), new Vertex(100, -100, -100), new Vertex(100, 100,
				 * 100), Color.GREEN)); tris.add(new Triangle(new Vertex(-100,
				 * 100, -100), new Vertex(100, -100, -100), new Vertex(-100,
				 * -100, 100), Color.BLUE));
				 */

				// cube
				/*
				 * tris.add(new Triangle(new Vertex(-100, -100, 100), new
				 * Vertex(100, -100, 100), new Vertex(-100, 100, 100),
				 * Color.ORANGE)); tris.add(new Triangle(new Vertex(100, -100,
				 * 100), new Vertex(100, 100, 100), new Vertex(-100, 100, 100),
				 * Color.ORANGE));
				 * 
				 * tris.add(new Triangle(new Vertex(100, -100, 100), new
				 * Vertex(100, -100, -100), new Vertex(100, 100, 100),
				 * Color.RED)); tris.add(new Triangle(new Vertex(100, -100,
				 * -100), new Vertex(100, 100, -100), new Vertex(100, 100, 100),
				 * Color.RED));
				 * 
				 * tris.add(new Triangle(new Vertex(-100, -100, -100), new
				 * Vertex(100, -100, -100), new Vertex(-100, 100, -100),
				 * Color.YELLOW)); tris.add(new Triangle(new Vertex(100, -100,
				 * -100), new Vertex(100, 100, -100), new Vertex(-100, 100,
				 * -100), Color.YELLOW));
				 * 
				 * tris.add(new Triangle(new Vertex(-100, -100, 100), new
				 * Vertex(-100, -100, -100), new Vertex(-100, 100, 100),
				 * Color.GREEN)); tris.add(new Triangle(new Vertex(-100, -100,
				 * -100), new Vertex(-100, 100, -100), new Vertex(-100, 100,
				 * 100), Color.GREEN));
				 * 
				 * tris.add(new Triangle(new Vertex(-100, -100, -100), new
				 * Vertex(100, -100, -100), new Vertex(-100, -100, 100),
				 * Color.MAGENTA)); tris.add(new Triangle(new Vertex(100, -100,
				 * -100), new Vertex(100, -100, 100), new Vertex(-100, -100,
				 * 100), Color.MAGENTA));
				 * 
				 * tris.add(new Triangle(new Vertex(-100, 100, -100), new
				 * Vertex(100, 100, -100), new Vertex(-100, 100, 100),
				 * Color.BLUE)); tris.add(new Triangle(new Vertex(100, 100,
				 * -100), new Vertex(100, 100, 100), new Vertex(-100, 100, 100),
				 * Color.BLUE));
				 */

				new Cubeisch(new Vertex(-100, -100, 100), new Vertex(100, -100, 100), new Vertex(100, 100, 100),
						new Vertex(-100, 100, 100), new Vertex(-100, -100, -100), new Vertex(100, -100, -100),
						new Vertex(100, 100, -100), new Vertex(-100, 100, -100), Color.ORANGE, Color.RED, Color.YELLOW,
						Color.GREEN, Color.MAGENTA, Color.BLUE, tris);
				
				System.out.println(tris.size());

				Vertex lightSource = new Vertex(0, 0, 1);

				double heading = Math.toRadians(headingSlider.getValue());
				Matrix3 headingTransform = new Matrix3(new double[] { Math.cos(heading), 0, Math.sin(heading), 0, 1, 0,
						-Math.sin(heading), 0, Math.cos(heading) });
				double pitch = Math.toRadians(pitchSlider.getValue());
				Matrix3 pitchTransform = new Matrix3(new double[] { 1, 0, 0, 0, Math.cos(pitch), Math.sin(pitch), 0,
						-Math.sin(pitch), Math.cos(pitch) });
				Matrix3 transform = headingTransform.multiply(pitchTransform);

				BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

				double[] zBuffer = new double[img.getWidth() * img.getHeight()];
				// initialize array with extremely far away depths
				for (int q = 0; q < zBuffer.length; q++) {
					zBuffer[q] = Double.NEGATIVE_INFINITY;
				}

				for (Triangle t : tris) {
					Vertex v1 = transform.transform(t.v1);
					Vertex v2 = transform.transform(t.v2);
					Vertex v3 = transform.transform(t.v3);

					Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
					Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
					Vertex norm = new Vertex(ab.y * ac.z - ab.z * ac.y, ab.z * ac.x - ab.x * ac.z,
							ab.x * ac.y - ab.y * ac.x);
					double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
					norm.x /= normalLength;
					norm.y /= normalLength;
					norm.z /= normalLength;

					double angleCos = Math
							.abs(lightSource.x * norm.x + lightSource.y * norm.y + lightSource.z * norm.z);

					// since we are not using Graphics2D anymore,
					// we have to do translation manually
					v1.x += getWidth() / 2;
					v1.y += getHeight() / 2;
					v2.x += getWidth() / 2;
					v2.y += getHeight() / 2;
					v3.x += getWidth() / 2;
					v3.y += getHeight() / 2;

					// compute rectangular bounds for triangle
					int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
					int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
					int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
					int maxY = (int) Math.min(img.getHeight() - 1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

					double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

					for (int y = minY; y <= maxY; y++) {
						for (int x = minX; x <= maxX; x++) {
							double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
							double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
							double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
							if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {

								// for each rasterized pixel:
								double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
								int zIndex = y * img.getWidth() + x;
								if (zBuffer[zIndex] < depth) {
									img.setRGB(x, y, getShade(t.color, angleCos).getRGB());
									zBuffer[zIndex] = depth;
								}
							}

						}
					}

				}

				g2.drawImage(img, 0, 0, null);

			}
		};
		pane.add(renderPanel, BorderLayout.CENTER);

		headingSlider.addChangeListener(e -> renderPanel.repaint());
		pitchSlider.addChangeListener(e -> renderPanel.repaint());

		frame.setSize(400, 400);
		frame.setVisible(true);
	}

	public static Color getShade(Color color, double shade) {
		double redLinear = Math.pow(color.getRed(), 2.4) * shade;
		double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
		double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

		int red = (int) Math.pow(redLinear, 1 / 2.4);
		int green = (int) Math.pow(greenLinear, 1 / 2.4);
		int blue = (int) Math.pow(blueLinear, 1 / 2.4);

		return new Color(red, green, blue);
	}

}

class Vertex {
	double x, y, z;

	Vertex(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}

class Triangle {
	Vertex v1, v2, v3;
	Color color;

	Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.color = color;
	}

}

class Quadrilateral {
	Triangle t1, t2;
	Color color;

	Quadrilateral(Vertex v1, Vertex v2, Vertex v3, Vertex v4, List<Triangle> tris) {
		this.t1 = new Triangle(v1, v2, v4, color);
		this.t2 = new Triangle(v2, v3, v4, color);

		tris.add(t1);
		tris.add(t2);
	}
}

class Cubeisch {
	Triangle[] t;
	Color c1, c2, c3, c4, c5, c6;

	Cubeisch(Vertex v1, Vertex v2, Vertex v3, Vertex v4, Vertex v5, Vertex v6, Vertex v7, Vertex v8, Color c1, Color c2,
			Color c3, Color c4, Color c5, Color c6, List<Triangle> tris) {
		this.t[0] = new Triangle(v1, v2, v4, c1);
		this.t[1] = new Triangle(v2, v3, v4, c1);
		this.t[2] = new Triangle(v2, v6, v3, c2);
		this.t[3] = new Triangle(v6, v7, v3, c2);
		this.t[4] = new Triangle(v5, v6, v8, c3);
		this.t[5] = new Triangle(v6, v7, v8, c3);
		this.t[6] = new Triangle(v5, v1, v8, c4);
		this.t[7] = new Triangle(v1, v2, v8, c4);
		this.t[8] = new Triangle(v5, v6, v1, c5);
		this.t[9] = new Triangle(v6, v2, v1, c5);
		this.t[10] = new Triangle(v8, v7, v4, c6);
		this.t[11] = new Triangle(v7, v3, v4, c6);

		for (int i = 0; i < 12; i++) {
			System.out.println("looop");
			tris.add(t[i]);
		}

	}
}

class Matrix3 {
	double[] values;

	Matrix3(double[] values) {
		this.values = values;
	}

	Matrix3 multiply(Matrix3 other) {
		double[] result = new double[9];
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				for (int i = 0; i < 3; i++) {
					result[row * 3 + col] += this.values[row * 3 + i] * other.values[i * 3 + col];
				}
			}
		}
		return new Matrix3(result);
	}

	Vertex transform(Vertex in) {
		return new Vertex(in.x * values[0] + in.y * values[3] + in.z * values[6],
				in.x * values[1] + in.y * values[4] + in.z * values[7],
				in.x * values[2] + in.y * values[5] + in.z * values[8]);
	}
}
