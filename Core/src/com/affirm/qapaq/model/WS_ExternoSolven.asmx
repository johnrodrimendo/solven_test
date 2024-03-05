<?xml version="1.0" encoding="utf-8"?>
<wsdl:definitions xmlns:s="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://schemas.xmlsoap.org/wsdl/soap12/" xmlns:http="http://schemas.xmlsoap.org/wsdl/http/" xmlns:mime="http://schemas.xmlsoap.org/wsdl/mime/" xmlns:tns="http://tempuri.org/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:tm="http://microsoft.com/wsdl/mime/textMatching/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" targetNamespace="http://tempuri.org/" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">
  <wsdl:types>
    <s:schema elementFormDefault="qualified" targetNamespace="http://tempuri.org/">
      <s:element name="B_CapturarValor">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="Opt" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="Username" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="Llave" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="B_CapturarValorResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="B_CapturarValorResult" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="string" nillable="true" type="s:string" />
    </s:schema>
  </wsdl:types>
  <wsdl:message name="B_CapturarValorSoapIn">
    <wsdl:part name="parameters" element="tns:B_CapturarValor" />
  </wsdl:message>
  <wsdl:message name="B_CapturarValorSoapOut">
    <wsdl:part name="parameters" element="tns:B_CapturarValorResponse" />
  </wsdl:message>
  <wsdl:message name="B_CapturarValorHttpGetIn">
    <wsdl:part name="Opt" type="s:string" />
    <wsdl:part name="Identificacion" type="s:string" />
    <wsdl:part name="Username" type="s:string" />
    <wsdl:part name="Llave" type="s:string" />
  </wsdl:message>
  <wsdl:message name="B_CapturarValorHttpGetOut">
    <wsdl:part name="Body" element="tns:string" />
  </wsdl:message>
  <wsdl:message name="B_CapturarValorHttpPostIn">
    <wsdl:part name="Opt" type="s:string" />
    <wsdl:part name="Identificacion" type="s:string" />
    <wsdl:part name="Username" type="s:string" />
    <wsdl:part name="Llave" type="s:string" />
  </wsdl:message>
  <wsdl:message name="B_CapturarValorHttpPostOut">
    <wsdl:part name="Body" element="tns:string" />
  </wsdl:message>
  <wsdl:portType name="WS_ExternoSolvenSoap">
    <wsdl:operation name="B_CapturarValor">
      <wsdl:input message="tns:B_CapturarValorSoapIn" />
      <wsdl:output message="tns:B_CapturarValorSoapOut" />
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:portType name="WS_ExternoSolvenHttpGet">
    <wsdl:operation name="B_CapturarValor">
      <wsdl:input message="tns:B_CapturarValorHttpGetIn" />
      <wsdl:output message="tns:B_CapturarValorHttpGetOut" />
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:portType name="WS_ExternoSolvenHttpPost">
    <wsdl:operation name="B_CapturarValor">
      <wsdl:input message="tns:B_CapturarValorHttpPostIn" />
      <wsdl:output message="tns:B_CapturarValorHttpPostOut" />
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:binding name="WS_ExternoSolvenSoap" type="tns:WS_ExternoSolvenSoap">
    <soap:binding transport="http://schemas.xmlsoap.org/soap/http" />
    <wsdl:operation name="B_CapturarValor">
      <soap:operation soapAction="http://tempuri.org/B_CapturarValor" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:binding name="WS_ExternoSolvenSoap12" type="tns:WS_ExternoSolvenSoap">
    <soap12:binding transport="http://schemas.xmlsoap.org/soap/http" />
    <wsdl:operation name="B_CapturarValor">
      <soap12:operation soapAction="http://tempuri.org/B_CapturarValor" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:binding name="WS_ExternoSolvenHttpGet" type="tns:WS_ExternoSolvenHttpGet">
    <http:binding verb="GET" />
    <wsdl:operation name="B_CapturarValor">
      <http:operation location="/B_CapturarValor" />
      <wsdl:input>
        <http:urlEncoded />
      </wsdl:input>
      <wsdl:output>
        <mime:mimeXml part="Body" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:binding name="WS_ExternoSolvenHttpPost" type="tns:WS_ExternoSolvenHttpPost">
    <http:binding verb="POST" />
    <wsdl:operation name="B_CapturarValor">
      <http:operation location="/B_CapturarValor" />
      <wsdl:input>
        <mime:content type="application/x-www-form-urlencoded" />
      </wsdl:input>
      <wsdl:output>
        <mime:mimeXml part="Body" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:service name="WS_ExternoSolven">
    <wsdl:port name="WS_ExternoSolvenSoap" binding="tns:WS_ExternoSolvenSoap">
      <soap:address location="http://qaalianzas.qapaq.pe/Ws_ESolven/WS_ExternoSolven.asmx" />
    </wsdl:port>
    <wsdl:port name="WS_ExternoSolvenSoap12" binding="tns:WS_ExternoSolvenSoap12">
      <soap12:address location="http://qaalianzas.qapaq.pe/Ws_ESolven/WS_ExternoSolven.asmx" />
    </wsdl:port>
    <wsdl:port name="WS_ExternoSolvenHttpGet" binding="tns:WS_ExternoSolvenHttpGet">
      <http:address location="http://qaalianzas.qapaq.pe/Ws_ESolven/WS_ExternoSolven.asmx" />
    </wsdl:port>
    <wsdl:port name="WS_ExternoSolvenHttpPost" binding="tns:WS_ExternoSolvenHttpPost">
      <http:address location="http://qaalianzas.qapaq.pe/Ws_ESolven/WS_ExternoSolven.asmx" />
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>