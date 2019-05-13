package zackary_zysk_estes;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Main 
{
	public static int imageWidth=0;
	public static int imageHeight=0;
	public static ArrayList<String> staffCoordinates=new ArrayList<String>();
	public static void main(String[]args) throws IOException
	{
		Scanner scan=new Scanner(System.in);
		FileWriter fileWriter = new FileWriter("/home/zzyskestes19/Desktop/RPG/Sheet Music Scanner/Sheet Music Scanner/src/zackary_zysk_estes/output.txt");
		PrintWriter printWriter = new PrintWriter(fileWriter);
		BufferedImage img = ImageIO.read(new File("/home/zzyskestes19/Desktop/RPG/Sheet Music Scanner/Sheet Music Scanner/src/zackary_zysk_estes/D_Major_Scale-page-001.jpg"));
		getImageDims(img);
		int[][] image = new int[imageHeight][imageWidth];
		int red;
		int green;
		int blue;
		int blackPixelCountStaff=0;
		int systemSkipCount=0;
		int bugDetection=0;
		int staffCount=0;
		System.out.println(imageWidth+","+imageHeight);
		//Converting image into array of RGB values
		image=getRGBPixelsIntoArray(img);
		//Switching everything to either Black or White
		for(int i=0; i<image.length;i++)
		{
			for(int j=0; j<image[i].length;j++)
			{
				red = (image[i][j] >> 16) & 0xFF;
				green = (image[i][j] >>8 ) & 0xFF;
				blue = (image[i][j]>>0) & 0xFF;
				//this assumes RGB values are the same
				if(red>130)
				{
					image[i][j]=0xFFFFFFFF;
				}
				if(red<=130)
				{
					image[i][j]=0xFF000000;
				}
			}
		}
		//Detecting where Staff starts
		getStaff(image,blackPixelCountStaff,systemSkipCount,staffCount);
		System.out.println(staffCoordinates);
		Object[] objects=staffCoordinates.toArray();
		String[] coordinates=Arrays.asList(objects).toArray(new String[0]);
		
	}
	private static void getImageDims(BufferedImage image){
		imageWidth=image.getWidth();
		imageHeight=image.getHeight();
	}
	private static int[][] getRGBPixelsIntoArray(BufferedImage image) {
		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int[][] result = new int[height][width];
		if (hasAlphaChannel) {
			final int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
				argb += ((int) pixels[pixel + 1] & 0xff << 16); // red
				argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 3] & 0xff)); // blue
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		} else {
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += -16777216; // 255 alpha
				argb += (((int) pixels[pixel] & 0xff) <<16); // red
				argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 2] & 0xff)); // blue
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		}

		return result;
	}

	private static void getStaff(int[][] a, int b, int c, int d)
	{
		int[][]image=a;
		int blackPixelCountStaff=b;
		int systemSkipCount=c;
		for(int i=0; i<image.length; i++)
		{
			for(int j=0; j<image[i].length; j++)
			{
				if(image[i][j]==0xFF000000)
				{
					blackPixelCountStaff++;
				}
				if(image[i][j]==0xFFFFFFFF)
				{
					blackPixelCountStaff=0;
				}
				if(blackPixelCountStaff>=imageWidth/2 && systemSkipCount==0)//found top of staff
				{
					systemSkipCount++;
					blackPixelCountStaff=0;
					String staffCoordinateY=Integer.toString(i);
					String staffCoordinateX=Integer.toString(j);
					String coordinate=staffCoordinateY+" "+staffCoordinateX;
					staffCoordinates.add(coordinate);
				}
				else
				{
					if(blackPixelCountStaff>=imageWidth/2&&systemSkipCount>0&&systemSkipCount<5) //found 3 lines in-between top and bottom
					{
						systemSkipCount++;
						blackPixelCountStaff=0;
					}
					else
					{
						if(blackPixelCountStaff>=imageWidth/2&&systemSkipCount==5) //found bottom of staff
						{
							systemSkipCount=0;
						}
					}
				}
			}
		}
	}
}
