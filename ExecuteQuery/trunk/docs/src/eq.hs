<?xml version="1.0"?>

<!DOCTYPE helpset   
PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
         "http://java.sun.com/products/javahelp/helpset_1_0.dtd">

<helpset version="1.0">
  
  <title>Execute Query Help</title>
  
  <maps>
    <homeID>introduction</homeID>
    <mapref location="helpmap.jhm"/>
  </maps>

  <view>
    <name>Contents</name>
    <label>Contents</label>
    <type>javax.help.TOCView</type>
    <data>toc.xml</data>
  </view>

  <view>
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
      jhelpsearch
    </data>
  </view>

  <presentation default="true"
                displayviewimages="false">
    <toolbar>
      <helpaction image="back_button">javax.help.BackAction</helpaction>
      <helpaction image="forward_button">javax.help.ForwardAction</helpaction>
      <helpaction>javax.help.SeparatorAction</helpaction>
      <helpaction image="print_button">javax.help.PrintAction</helpaction>
      <helpaction image="page_setup_button">javax.help.PrintSetupAction</helpaction>
    </toolbar>
  </presentation>
  
  <!--
  <view>
    <name>Index</name>
    <label>Help Index</label>
    <type>javax.help.IndexView</type>
    <data>eqindex.xml</data>
  </view>
  
  
  <view>
    <name>Search</name>
    <label>Search Help</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
      jhelpsearch</data>
  </view>
  -->
  
</helpset>
