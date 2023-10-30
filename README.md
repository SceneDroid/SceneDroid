# SceneDroid

Automated Generation of GUI Scenes for Android Apps.

Demo Video: https://www.youtube.com/watch?v=3A-tGi0Z4fM

## 0x1 Abstract

Due to the competitive environment, mobile apps are usually produced under pressure with lots of complicated functionality and
UI pages. Therefore, it is challenging for various roles to design, understand, test, and maintain these apps. The extracted transition graphs for apps such as ATG, WTG, and STG have a low transition coverage and coarse-grained granularity, which limits the existing methods of graphical user interface (GUI) modeling by UI exploration. In this paper, we propose SceneDroid, a dynamic exploration approach to extracting the GUI scenes dynamically by combining smart exploration, state fuzzing, and indirect launching strategies. We present the GUI scenes as a scene transitiongraph (SceneTG) to model the GUI of apps with high transition coverage and fine-grained granularity. Apart from the effectiveness evaluation of SceneDroid, we also illustrate the future potential of SceneDroid to support app development, reverse engineering, and GUI regression testing. 

## 0x2 File Structure

- sourcecode：Source code of the tool
- experimentaldata：RQ1～3 Statistical data
