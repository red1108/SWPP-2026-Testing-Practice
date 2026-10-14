"""Record actual JUnit XML outcomes; no inferred or synthetic pass/fail results."""
import json
from pathlib import Path
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
summary = {}
for kind, folder in {
    "unit": "app/build/test-results/testDebugUnitTest",
    "integration": "app/build/outputs/androidTest-results/connected",
}.items():
    suites = []
    for path in sorted((root / folder).rglob("TEST-*.xml")):
        suite = ET.parse(path).getroot()
        cases = []
        for case in suite.findall("testcase"):
            failure = case.find("failure")
            error = case.find("error")
            problem = failure if failure is not None else error
            cases.append({
                "name": case.get("name"),
                "class": case.get("classname"),
                "status": "failed" if problem is not None else
                          "skipped" if case.find("skipped") is not None else "passed",
                "message": problem.get("message", "") if problem is not None else "",
            })
        suites.append({
            "file": str(path.relative_to(root)),
            "name": suite.get("name"),
            "tests": int(suite.get("tests", 0)),
            "failures": int(suite.get("failures", 0)),
            "errors": int(suite.get("errors", 0)),
            "skipped": int(suite.get("skipped", 0)),
            "cases": cases,
        })
    summary[kind] = suites
output = root / "output/evidence/local-results.json"
output.parent.mkdir(parents=True, exist_ok=True)
output.write_text(json.dumps(summary, indent=2) + "\n")
for kind, suites in summary.items():
    print(kind, {key: sum(s[key] for s in suites)
                 for key in ["tests", "failures", "errors", "skipped"]})
