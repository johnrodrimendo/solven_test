<?xml version = "1.0" encoding = "utf-8"?>
<definitions name="ws_senestliteRSA" targetNamespace="PrivadoE2Cliente" xmlns:wsdlns="PrivadoE2Cliente" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://schemas.xmlsoap.org/wsdl/" xmlns:tns="PrivadoE2Cliente">
	<types>
		<schema targetNamespace="PrivadoE2Cliente" xmlns="http://www.w3.org/2001/XMLSchema" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" elementFormDefault="qualified">
			<complexType name="sdt_respuesta_consulta_rapidaRSA">
				<sequence>
					<element name="Documento" type="xsd:string">
					</element>
					<element name="RazonSocial" type="xsd:string">
					</element>
					<element name="FechaNacimiento" type="xsd:string">
					</element>
					<element name="FechaProceso" type="xsd:string">
					</element>
					<element name="Semaforos" type="xsd:string">
					</element>
					<element name="Nota" type="xsd:string">
					</element>
					<element name="NroBancos" type="xsd:string">
					</element>
					<element name="DeudaTotal" type="xsd:string">
					</element>
					<element name="VencidoBanco" type="xsd:string">
					</element>
					<element name="Calificativo" type="xsd:string">
					</element>
					<element name="SemaActual" type="xsd:string">
					</element>
					<element name="SemaPrevio" type="xsd:string">
					</element>
					<element name="SemaPeorMejor" type="xsd:string">
					</element>
					<element name="Documento2" type="xsd:string">
					</element>
					<element name="EstDomic" type="xsd:string">
					</element>
					<element name="CondDomic" type="xsd:string">
					</element>
					<element name="DeudaTributaria" type="xsd:string">
					</element>
					<element name="DeudaLaboral" type="xsd:string">
					</element>
					<element name="DeudaImpaga" type="xsd:string">
					</element>
					<element name="DeudaProtestos" type="xsd:string">
					</element>
					<element name="DeudaSBS" type="xsd:string">
					</element>
					<element name="TarCtas" type="xsd:string">
					</element>
					<element name="RepNeg" type="xsd:string">
					</element>
					<element name="TipoActv" type="xsd:string">
					</element>
					<element name="FechIniActv" type="xsd:string">
					</element>
					<element name="DireccionFiscal" type="xsd:string">
					</element>
					<element name="CodigoWS" type="xsd:string">
					</element>
				</sequence>
			</complexType>
			<element name="ws_senestliteRSA.Execute">
				<complexType>
					<sequence>
						<element minOccurs="1" maxOccurs="1" name="Gx_usuenc" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Gx_pasenc" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Gx_key" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Tipodoc" type="xsd:string" />
						<element minOccurs="1" maxOccurs="1" name="Nrodoc" type="xsd:string" />
					</sequence>
				</complexType>
			</element>
			<element name="ws_senestliteRSA.ExecuteResponse">
				<complexType>
					<sequence>
						<element minOccurs="1" maxOccurs="1" name="Resultado" type="tns:sdt_respuesta_consulta_rapidaRSA" />
					</sequence>
				</complexType>
			</element>
		</schema>
	</types>
	<message name="ws_senestliteRSA.ExecuteSoapIn">
		<part name="parameters" element="tns:ws_senestliteRSA.Execute" />
	</message>
	<message name="ws_senestliteRSA.ExecuteSoapOut">
		<part name="parameters" element="tns:ws_senestliteRSA.ExecuteResponse" />
	</message>
	<portType name="ws_senestliteRSASoapPort">
		<operation name="Execute">
			<input message="wsdlns:ws_senestliteRSA.ExecuteSoapIn" />
			<output message="wsdlns:ws_senestliteRSA.ExecuteSoapOut" />
		</operation>
	</portType>
	<binding name="ws_senestliteRSASoapBinding" type="wsdlns:ws_senestliteRSASoapPort">
		<soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http" />
		<operation name="Execute">
			<soap:operation soapAction="PrivadoE2Clienteaction/AWS_SENESTLITERSA.Execute" />
			<input>
				<soap:body use="literal" />
			</input>
			<output>
				<soap:body use="literal" />
			</output>
		</operation>
	</binding>
	<service name="ws_senestliteRSA">
		<port name="ws_senestliteRSASoapPort" binding="wsdlns:ws_senestliteRSASoapBinding">
			<soap:address location="https://www2.sentinelperu.com/wsevo2/aws_senestlitersa.aspx" />
		</port>
	</service>
</definitions>
