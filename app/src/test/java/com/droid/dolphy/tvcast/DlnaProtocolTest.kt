package com.droid.dolphy.tvcast

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DlnaProtocolTest {
    @Test
    fun ssdpResponseParsesCaseInsensitiveHeaders() {
        val result = parseSsdpAdvertisement(
            "HTTP/1.1 200 OK\r\nLocation: http://192.168.1.8:5000/root.xml\r\n" +
                "USN: uuid:renderer-1\r\nST: urn:schemas-upnp-org:device:MediaRenderer:1\r\n\r\n",
            "192.168.1.8",
        )
        assertEquals("http://192.168.1.8:5000/root.xml", result?.location)
        assertEquals("uuid:renderer-1", result?.usn)
        assertEquals("192.168.1.8", result?.address)
    }

    @Test
    fun ssdpWithoutLocationIsIgnored() {
        assertNull(parseSsdpAdvertisement("HTTP/1.1 200 OK\r\nUSN: uuid:x\r\n\r\n", "192.168.1.2"))
        assertNull(parseSsdpAdvertisement("NOTIFY * HTTP/1.1\r\nLOCATION: http://x/root.xml\r\n\r\n", "192.168.1.2"))
    }

    @Test
    fun avTransportDescriptionUsesRendererControlUrl() {
        val xml = """
            <root xmlns="urn:schemas-upnp-org:device-1-0">
              <device>
                <friendlyName>Living Room TV</friendlyName>
                <UDN>uuid:tv-42</UDN>
                <serviceList>
                  <service><serviceType>urn:schemas-upnp-org:service:ConnectionManager:1</serviceType><controlURL>/cm</controlURL></service>
                  <service><serviceType>urn:schemas-upnp-org:service:AVTransport:1</serviceType><controlURL>/avt/control</controlURL></service>
                </serviceList>
              </device>
            </root>
        """.trimIndent()
        val device = parseDlnaDeviceDescription(xml, "http://192.168.1.9:8080/description.xml")
        assertEquals("Living Room TV", device?.name)
        assertEquals("uuid:tv-42", device?.id)
        assertEquals("http://192.168.1.9:8080/avt/control", device?.controlUrl)
        assertEquals(CastProtocol.DLNA_UPNP, device?.protocol)
    }

    @Test
    fun relativeControlUrlAndNestedRendererAreResolved() {
        val xml = """
            <root><device><friendlyName>Root</friendlyName><deviceList><device>
            <friendlyName>Nested Renderer</friendlyName><serviceList><service>
            <serviceType>AVTransport</serviceType><controlURL>control/av</controlURL>
            </service></serviceList></device></deviceList></device></root>
        """.trimIndent()
        val device = parseDlnaDeviceDescription(xml, "http://10.0.0.5:49152/root/device.xml")
        assertEquals("Nested Renderer", device?.name)
        assertEquals("http://10.0.0.5:49152/root/control/av", device?.controlUrl)
    }

    @Test
    fun descriptionsWithoutAvTransportOrWithDoctypeAreRejected() {
        assertNull(parseDlnaDeviceDescription("<root><device><friendlyName>TV</friendlyName></device></root>", "http://x/root.xml"))
        assertNull(
            parseDlnaDeviceDescription(
                "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]><root><device><friendlyName>&xxe;</friendlyName></device></root>",
                "http://x/root.xml",
            ),
        )
    }

    @Test
    fun nonHttpControlUrlsAreRejected() {
        assertNull(resolveDlnaControlUrl("http://192.168.1.2/root.xml", "file:///tmp/control"))
        assertEquals(
            "http://192.168.1.2:9000/control",
            resolveDlnaControlUrl("http://192.168.1.2/root.xml", "http://192.168.1.2:9000/control"),
        )
    }
}
