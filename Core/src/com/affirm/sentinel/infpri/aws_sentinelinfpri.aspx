<?xml version = "1.0" encoding = "utf-8"?>
<definitions name="WS_SentinelInfPri" targetNamespace="PrivadoE2Cliente" xmlns:wsdlns="PrivadoE2Cliente" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://schemas.xmlsoap.org/wsdl/" xmlns:tns="PrivadoE2Cliente">
	<types>
		<schema targetNamespace="PrivadoE2Cliente" xmlns="http://www.w3.org/2001/XMLSchema" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" elementFormDefault="qualified">
			<complexType name="ArrayOfSDT_InfCopacEnt">
				<sequence>
					<element minOccurs="0" maxOccurs="unbounded" name="SDT_InfCopacEnt" type="tns:SDT_InfCopacEnt">
					</element>
				</sequence>
			</complexType>
			<complexType name="SDT_InfCopacEnt">
				<sequence>
					<element name="Entidad" type="xsd:string">
					</element>
					<element name="EntiFechaInf" type="xsd:string">
					</element>
					<element name="EntiSaldo" type="xsd:double">
					</element>
					<element name="EntiUltCal" type="xsd:string">
					</element>
					<element name="EntiPerCal12M" type="xsd:string">
					</element>
				</sequence>
			</complexType>
			<element name="WS_SentinelInfPri.Execute">
				<complexType>
					<sequence>
						<element minOccurs="1" maxOccurs="1" name="Gx_usuenc" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Gx_pasenc" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Gx_key" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Ti_tipodoc" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Ti_nrodoc" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Cg_tipodoc" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Cg_nrodoc" type="xsd:string" />
					</sequence>
				</complexType>
			</element>
			<element name="WS_SentinelInfPri.ExecuteResponse">
				<complexType>
					<sequence>
						<element minOccurs="1" maxOccurs="1" name="Ti_nombres" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Ti_paterno" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Ti_materno" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Ti_fchnac" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Ti_entidades" type="tns:ArrayOfSDT_InfCopacEnt" />
						<element minOccurs="1" maxOccurs="1" name="Ti_calddpult24" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Ti_montodocumento" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Cg_nombres" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Cg_paterno" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Cg_materno" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Cg_fchnac" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Cg_entidades" type="tns:ArrayOfSDT_InfCopacEnt" />
						<element minOccurs="1" maxOccurs="1" name="Cg_calddpult24" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Cg_montodocumento" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Codigows" type="xsd:string" />
					</sequence>
				</complexType>
			</element>
		</schema>
	</types>
	<message name="WS_SentinelInfPri.ExecuteSoapIn">
		<part name="parameters" element="tns:WS_SentinelInfPri.Execute" />
	</message>
	<message name="WS_SentinelInfPri.ExecuteSoapOut">
		<part name="parameters" element="tns:WS_SentinelInfPri.ExecuteResponse" />
	</message>
	<portType name="WS_SentinelInfPriSoapPort">
		<operation name="Execute">
			<input message="wsdlns:WS_SentinelInfPri.ExecuteSoapIn" />
			<output message="wsdlns:WS_SentinelInfPri.ExecuteSoapOut" />
		</operation>
	</portType>
	<binding name="WS_SentinelInfPriSoapBinding" type="wsdlns:WS_SentinelInfPriSoapPort">
		<soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http" />
		<operation name="Execute">
			<soap:operation soapAction="PrivadoE2Clienteaction/AWS_SENTINELINFPRI.Execute" />
			<input>
				<soap:body use="literal" />
			</input>
			<output>
				<soap:body use="literal" />
			</output>
		</operation>
	</binding>
	<service name="WS_SentinelInfPri">
		<port name="WS_SentinelInfPriSoapPort" binding="wsdlns:WS_SentinelInfPriSoapBinding">
			<soap:address location="https://www2.sentinelperu.com/wsevo2/aws_sentinelinfpri.aspx" />
		</port>
	</service>
</definitions>
