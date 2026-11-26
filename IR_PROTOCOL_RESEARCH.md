# IR protocol research — ROVEX UNITE RS-07CST5

## Confirmed from the user's nameplate
- Model: RS-07CST5 UNITE
- Outdoor unit manufacturer: Zhongshan Changhong Electric Co., Ltd.
- Refrigerant: R410A

## Confirmed in the UNITE manual
- It depicts a handheld remote and its functions.
- It says the indoor unit has an IR receiver.
- It describes an emergency button under the front panel that toggles Auto mode when the remote is missing.
- It does not state a carrier frequency, protocol name, frame length, or raw command bytes.

## Experimental candidate: GREE
The public IRremoteESP8266 implementation documents a GREE air-conditioner format with a 38 kHz carrier, 9000/4500 µs header, 620 µs marks, 1600/540 µs bit spaces, an 8-byte state and an inter-block 010 footer with a 19,980 µs gap. Its implementation labels sending as stable for GREE-family devices.

This only establishes that the GREE format is technically well-defined. It does **not** establish that ROVEX RS-07CST5 uses it. The outdoor-unit OEM being Changhong and the compressor brand being GREE/GMCC are not evidence of the indoor unit's remote protocol.

## Trial signal added to the app
- Candidate profile: GREE, 38 kHz.
- State bytes: `09 07 60 50 00 20 00 C0`.
- Interpreted preset: power ON, COOL, 23 °C, fan AUTO, YAW1F-style GREE model bit.
- The app shows a confirmation before sending and reports only that Android transmitted the carrier pattern; it cannot confirm that the AC received or accepted it.
- This is an experimental family-level test, not a verified ROVEX code.

## Sources
- ROVEX UNITE manual: https://cdn.vseinstrumenti.ru/instruction/nomenclaturecontent/202604/61450341.pdf/23191811_manual.pdf
- GREE protocol timing: https://crankyoldgit.github.io/IRremoteESP8266/doxygen/html/ir__Gree_8cpp.html
- GREE implementation and state bit layout: https://raw.githubusercontent.com/crankyoldgit/IRremoteESP8266/master/src/ir_Gree.cpp
- GREE state layout and supported remote families: https://raw.githubusercontent.com/crankyoldgit/IRremoteESP8266/master/src/ir_Gree.h
- GREE tests and captured examples: https://github.com/crankyoldgit/IRremoteESP8266/blob/master/test/ir_Gree_test.cpp
