# Licensed to Elasticsearch B.V. under one or more contributor
# license agreements. See the NOTICE file distributed with
# this work for additional information regarding copyright
# ownership. Elasticsearch B.V. licenses this file to you under
# the Apache License, Version 2.0 (the "License"); you may
# not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

require 'logstash/compiler/lscl/lscl_grammar'

java_import org.logstash.config.ir.PipelineIR
java_import org.logstash.config.ir.graph.Graph
java_import org.logstash.SyntaxCheck

module LogStash; class Compiler
  include ::LogStash::Util::Loggable

  def self.compile_imperative(source_with_metadata, support_escapes)
    if !source_with_metadata.is_a?(org.logstash.common.SourceWithMetadata)
      raise ArgumentError, "Expected 'org.logstash.common.SourceWithMetadata', got #{source_with_metadata.class}"
    end

    grammar = LogStashCompilerLSCLGrammarParser.new
    config = grammar.parse(source_with_metadata.text)

    if config.nil?
      raise ConfigurationError, grammar.failure_reason
    end

    config.process_escape_sequences = support_escapes
    config.compile(source_with_metadata)
  end

  def self.compile_graph(source_with_metadata, support_escapes)
    Hash[compile_imperative(source_with_metadata, support_escapes).map {|section,icompiled| [section, icompiled.toGraph]}]
  end
  
  def self.check_syntax(my_config)
    grammar = LogStashCompilerLSCLGrammarParser.new
    config_output = grammar.parse(my_config)
    
    if config_output.nil?
      return SyntaxCheck.new(grammar.failure_reason)
    end
    return SyntaxCheck.new()
  end
  
end; end
