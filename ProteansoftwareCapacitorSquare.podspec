
  Pod::Spec.new do |s|
    s.name = 'ProteansoftwareCapacitorSquare'
    s.version = '0.0.1'
    s.summary = 'Integrate with Square Payments SDK'
    s.license = 'MIT'
    s.homepage = 'https://github.com/ProteanSoftware/capacitor-square'
    s.author = 'Ashley Medway'
    s.source = { :git => 'https://github.com/ProteanSoftware/capacitor-square', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
    s.dependency 'SquarePointOfSaleSDK'
  end